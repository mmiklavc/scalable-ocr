package ocr.cli;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import net.sourceforge.tess4j.TesseractException;
import ocr.conversion.Converter;
import ocr.extraction.tesseract.TesseractUtil;
import ocr.preprocessing.conversion.CLIUtils;
import ocr.preprocessing.conversion.CleaningOptions;
import ocr.preprocessing.conversion.CommandFailedException;
import ocr.preprocessing.conversion.TextCleaner;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;
import java.util.function.Function;

public class CLI {
  public static enum OcrOptions {
    HELP("h", code -> {
      Option o = new Option(code, "help", false, "This screen");
      o.setRequired(false);
      return o;
    }),
    INPUT("i", code -> {
      Option o = new Option(code, "input", true, "Single Input File");
      o.setRequired(false);
      o.setArgName("INPUT");
      return o;
    }),
    INPUT_DIR("id", code -> {
      Option o = new Option(code, "input", true, "Input Directory");
      o.setRequired(false);
      o.setArgName("DIR");
      return o;
    }),
    INPUT_FILE("if", code -> {
      Option o = new Option(code, "input_file", true, "Input File");
      o.setRequired(false);
      o.setArgName("FILE");
      return o;
    }),
    OUTPUT("o", code -> {
      Option o = new Option(code, "output", true, "Output Directory");
      o.setRequired(false);
      o.setArgName("DIR");
      return o;
    }),
    PREPROCESSING("p", code -> {
      Option o = new Option(code, "preprocessing", true, "Preprocessing Config");
      o.setRequired(false);
      o.setArgName("Preprocessing Configs");
      return o;
    }),
    TEMP_DIR("t", code -> {
      Option o = new Option(code, "temp_dir", true, "Temp Dir");
      o.setRequired(false);
      o.setArgName("DIR");
      return o;
    }),
    LIB_PATH("l", code -> {
      Option o = new Option(code, "lib_path", true, "jna library path");
      o.setRequired(false);
      o.setArgName("DIR");
      return o;
    }),
    CONVERT_PATH("c", code -> {
      Option o = new Option(code, "convert_path", true, "Path to the Convert utility");
      o.setRequired(false);
      o.setArgName("PATH");
      return o;
    }),
    TESSDATA_PATH("d", code -> {
      Option o = new Option(code, "tess_data_path", true, "Path to TESS_DATA");
      o.setRequired(false);
      o.setArgName("PATH");
      return o;
    }),
    TESSPROPERTIES("D", code ->
     OptionBuilder.withArgName( "property=value" )
              .hasArgs(2)
              .withValueSeparator()
              .withDescription( "Tesseract variables" )
              .create( code )

    ),
    PHASE("ph", code -> {
      Option o = new Option(code, "phases", true, "Which phases to run: [convert|preprocess|ocr]");
      o.setRequired(false);
      o.setArgName("PHASE");
      return o;
    });
    Option option;
    String shortCode;
    OcrOptions(String shortCode
              , Function<String, Option> optionHandler
                 ) {
      this.shortCode = shortCode;
      this.option = optionHandler.apply(shortCode);

    }

    public boolean has(CommandLine cli) {
      return cli.hasOption(shortCode);
    }

    public String get(CommandLine cli) {
      return cli.getOptionValue(shortCode);
    }
    public String get(CommandLine cli, String def) {
      return has(cli)?cli.getOptionValue(shortCode):def;
    }

    public Map<String, String> getProperties(CommandLine cli) {
      Properties p = cli.getOptionProperties(shortCode);
      Map<String, String> ret = new HashMap<>();
      for(Map.Entry<Object, Object> kv : p.entrySet()) {
        ret.put(kv.getKey().toString(), kv.getValue().toString());
      }
      return ret;
    }


    public static CommandLine parse(CommandLineParser parser, String[] args) throws ParseException {
      try {
        CommandLine cli = parser.parse(getOptions(), args);
        if(HELP.has(cli)) {
          printHelp();
          System.exit(0);
        }
        return cli;
      } catch (ParseException e) {
        System.err.println("Unable to parse args: " + Joiner.on(' ').join(args));
        e.printStackTrace(System.err);
        printHelp();
        throw e;
      }
    }

    public static void printHelp() {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp( "OCRCLI", getOptions());
    }

    public static Options getOptions() {
      Options ret = new Options();
      for(OcrOptions o : OcrOptions.values()) {
        ret.addOption(o.option);
      }
      return ret;
    }
  }

  public static Set<String> getAlreadyProcessed(File outputDir) {
    Set<String> ret = new HashSet<>();
    for(File f : outputDir.listFiles()) {
      ret.add(stripSuffix(f.getName()));
    }
    return ret;
  }

  public static String stripSuffix(String filename) {
    if(filename.contains(".")) {
      return Iterables.getFirst(Splitter.on(".").split(filename), null);
    }
    else {
      return filename;
    }
  }

  public static List<File> filterFilesToProcess(Iterable<File> files, Set<String> alreadyProcessed) {
    List<File> ret = new ArrayList<>();
    for(File f : files) {
      if(!alreadyProcessed.contains(stripSuffix(f.getName()))) {
        ret.add(f);
      }
      else {
        System.out.println("Skipping " + f.getName());
      }
    }
    return ret;
  }

  public static List<File> extractFilesFromFile(File inputFile) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(inputFile));
    List<File> ret = new ArrayList<>();
    for(String line = null; (line = br.readLine()) != null;) {
      ret.add(new File(line));
    }
    return ret;
  }

  public static List<File> extractFilesFromDirectory(File inputDir) throws IOException {
    List<File> ret = new ArrayList<>();
    for(File f : inputDir.listFiles()) {
      ret.add(f);
    }
    return ret;
  }

  public static void main(String... argv) throws ParseException, IOException, CommandFailedException, TesseractException {
    PosixParser parser = new PosixParser();
    CommandLine cli = OcrOptions.parse(parser, argv);
    String phase = "all";
    if(OcrOptions.PHASE.has(cli)) {
      phase = OcrOptions.PHASE.get(cli);
    }
    System.getProperties().setProperty("jna.library.path", OcrOptions.LIB_PATH.get(cli, "/opt/local/lib"));
    String preprocessingDef = OcrOptions.PREPROCESSING.get(cli);
    String tempDirStr = OcrOptions.TEMP_DIR.get(cli, "/tmp");
    List<File> files = null;
    File outDir = new File(OcrOptions.OUTPUT.get(cli, "."));
    Set<String> alreadyProcessed = getAlreadyProcessed(outDir);
    Map<String, String> tessProperties = OcrOptions.TESSPROPERTIES.getProperties(cli);
    if(OcrOptions.INPUT.has(cli)) {
      files = Arrays.asList(new File(OcrOptions.INPUT.get(cli)));
    } else if(OcrOptions.INPUT_FILE.has(cli)) {
      files = filterFilesToProcess(extractFilesFromFile(new File(OcrOptions.INPUT_FILE.get(cli))), alreadyProcessed);
    } else if(OcrOptions.INPUT_DIR.has(cli)){
      files = filterFilesToProcess(extractFilesFromDirectory(new File(OcrOptions.INPUT_DIR.get(cli))), alreadyProcessed);
    } else {
      throw new IllegalStateException("Must specify one of input, input directory or input file");
    }
    File tempDir = new File(tempDirStr);
    String convertPath = OcrOptions.CONVERT_PATH.get(cli, "/usr/local/bin/convert");
    File tessDataPath = new File(OcrOptions.TESSDATA_PATH.get(cli,"/usr/local/Cellar/tesseract/3.04.01_1/share/tessdata/"));
    CommandLine cleaningCli = CleaningOptions.parse(new DefaultParser(), CLIUtils.translateCommandline(preprocessingDef) );
    final TextCleaner cleaner = CleaningOptions.createTextCleaner(cleaningCli, convertPath, tempDirStr);
    int i = 0;
    for(File f : files) {
      System.out.println("Processing " + f.getName() + " (" + i++ + " / " + files.size()+ ")");
      int pageNumber = 0;
      if("all".equals(phase)) {
        for (Map.Entry<File, Boolean> page : toPages(new BufferedInputStream(new FileInputStream(f)), tempDir)) {
          pageNumber++;
          System.out.println("Page " + pageNumber);
          try {
            if (page.getValue()) {
              byte[] converted = cleaner.convert(new BufferedInputStream(new FileInputStream(page.getKey())));
              writePreprocessed(converted, new File(outDir, f.getName() + "-" + pageNumber + ".tiff"));
              String pageText = TesseractUtil.INSTANCE.ocr(converted, tessDataPath, tessProperties);
              String fileName = f.getName() + "-" + pageNumber + ".txt";
              File outFile = new File(outDir, fileName);
              try (PrintWriter pw = new PrintWriter(outFile)) {
                IOUtils.write(pageText, pw);
                pw.flush();
              }
            }
          } finally {
            page.getKey().delete();
          }
        }
      } else {
        switch(phase) {
          case "convert" :
            toPages(new BufferedInputStream(new FileInputStream(f)), outDir);
            return;
          case "preprocess" :
            byte[] converted = cleaner.convert(new BufferedInputStream(new FileInputStream(f)));
            writePreprocessed(converted, new File(outDir, f.getName() + "-" + pageNumber + ".tiff"));
            return;
          case "ocr" :
            try (FileInputStream fis = new FileInputStream(f)) {
              byte[] inFile = IOUtils.toByteArray(fis);
              String pageText = TesseractUtil.INSTANCE.ocr(inFile, tessDataPath, tessProperties);
              String fileName = f.getName() + "-" + pageNumber + ".txt";
              File outFile = new File(outDir, fileName);
              try (PrintWriter pw = new PrintWriter(outFile)) {
                IOUtils.write(pageText, pw);
                pw.flush();
              }
            }
            return;
          default :
            throw new IllegalArgumentException("Unknown phase: " + phase);
        }
      }
    }
  }

  private static void writePreprocessed(byte[] converted, File file) {
    try(FileOutputStream fos = new FileOutputStream(file)) {
      IOUtils.write(converted, fos);
      fos.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static List<Map.Entry<File, Boolean>> toPages(InputStream in, File tempDir) {
    Converter converter = new Converter();
    if(!tempDir.exists()) {
      tempDir.mkdirs();
    }
    List<Map.Entry<File, Boolean>> ret = new ArrayList<>();
    for(Map.Entry<File, Boolean> kv : converter.toJava(converter.convert(in, tempDir))) {
      ret.add(new AbstractMap.SimpleEntry<>(kv.getKey(), kv.getValue()));
    }
    return ret;
  }
}
