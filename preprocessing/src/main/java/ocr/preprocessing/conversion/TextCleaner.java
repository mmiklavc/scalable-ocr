package ocr.preprocessing.conversion;

import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;

public class TextCleaner {
  public interface Aliased {
    String getAlias();
  }
  public enum Rotation implements Aliased {
    CLOCKWISE("cw"), COUNTERCLOCKWISE("ccw"), NONE("none");
    String alias;
    Rotation(String alias) {
      this.alias = alias;
    }

    @Override
    public String getAlias() {
      return alias;
    }

    public static Rotation getDefault() { return NONE;}
  }

  public enum Layout implements Aliased {
    PORTRAIT("portrait"), LANDSCAPE("landscape")
    ;
    String alias;
    Layout(String alias) {
      this.alias = alias;
    }

    @Override
    public String getAlias() {
      return alias;
    }

    public static Layout getDefault() { return PORTRAIT;}
  }
  public enum Enhance implements Aliased {
    STRETCH("stretch"), NORMALIZE("normalize"), NONE("none")
    ;
    String alias;
    Enhance(String alias) {
      this.alias = alias;
    }

    @Override
    public String getAlias() {
      return alias;
    }

    public static Enhance getDefault() { return NONE;}
  }


  private Rotation rotate = Rotation.getDefault();
  private Layout layout = Layout.getDefault();
  private boolean grayscale = false;
  private Enhance enhance = Enhance.getDefault();
  private int filterSize = 15;
  private int offset = 5;
  private Optional<Integer> threshold = Optional.empty();
  private int sharpAmt = 0;
  private int saturation = 200;
  private int adaptiveBlur = 0;
  private boolean unrotate = false;
  private boolean trim = false;
  private int padAmt = 0;
  private String bgColor = "white";
  private Optional<String> convertPath = Optional.empty();
  private Optional<String> tmpPath = Optional.empty();
  private Iterable<Handler> handlers;

  public TextCleaner(Iterable<Handler> handlers
                    ,CommandLine cli
                    )
  {
    this(handlers, cli, null, null);
  }
  public TextCleaner(Iterable<Handler> handlers
                    ,CommandLine cli
                    ,String convertPath
                    ,String tmpPath
                    )
  {
    if(convertPath != null) {
      this.convertPath = Optional.of(convertPath);
    }
    if(tmpPath != null) {
      this.tmpPath = Optional.of(tmpPath);
    }
    this.handlers = handlers;
    for(Handler h : handlers) {
      h.set(cli, this);
    }
  }

  public Rotation getRotate() {
    return rotate;
  }

  public void setRotate(Rotation rotate) {
    this.rotate = rotate;
  }

  public Layout getLayout() {
    return layout;
  }

  public void setLayout(Layout layout) {
    this.layout = layout;
  }

  public boolean isGrayscale() {
    return grayscale;
  }

  public void setGrayscale(boolean grayscale) {
    this.grayscale = grayscale;
  }

  public Enhance getEnhance() {
    return enhance;
  }

  public void setEnhance(Enhance enhance) {
    this.enhance = enhance;
  }

  public int getFilterSize() {
    return filterSize;
  }

  public void setFilterSize(int filterSize) {
    this.filterSize = filterSize;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public Optional<Integer> getThreshold() {
    return threshold;
  }

  public void setThreshold(Optional<Integer> threshold) {
    this.threshold = threshold;
  }

  public int getSharpAmt() {
    return sharpAmt;
  }

  public void setSharpAmt(int sharpAmt) {
    this.sharpAmt = sharpAmt;
  }

  public int getSaturation() {
    return saturation;
  }

  public void setSaturation(int saturation) {
    this.saturation = saturation;
  }

  public int getAdaptiveBlur() {
    return adaptiveBlur;
  }

  public void setAdaptiveBlur(int adaptiveBlur) {
    this.adaptiveBlur = adaptiveBlur;
  }

  public boolean isUnrotate() {
    return unrotate;
  }

  public void setUnrotate(boolean unrotate) {
    this.unrotate = unrotate;
  }

  public boolean isTrim() {
    return trim;
  }

  public void setTrim(boolean trim) {
    this.trim = trim;
  }

  public int getPadAmt() {
    return padAmt;
  }

  public void setPadAmt(int padAmt) {
    this.padAmt = padAmt;
  }

  public String getBgColor() {
    return bgColor;
  }

  public void setBgColor(String bgColor) {
    this.bgColor = bgColor;
  }

  private File getTmpOutputFile(String suffix) throws IOException {
    String dottedSuffix = suffix.charAt(0) == '.'?suffix:("." + suffix);
    if(tmpPath.isPresent()) {
      return File.createTempFile("textCleaner", dottedSuffix, new File(tmpPath.get()));
    }
    else {
      return File.createTempFile("textCleaner", dottedSuffix);
    }
  }

  public String[] getCommandLine( String inputFile
                              , String outputFile
                              ) throws IOException
  {
    BufferedImage img = ImageUtils.INSTANCE.readImage(new File(inputFile));
    int height = ImageUtils.INSTANCE.getHeight(img);
    int width = ImageUtils.INSTANCE.getWidth(img);
    int aspectRatio = 0;
    {
      double a = ImageUtils.INSTANCE.getAspectRatio(img);
      if(a >= 1) {
        aspectRatio = 1;
      }
    }
    EnumMap<CleaningOptions, String> options = getOptions(aspectRatio);
    List<String> ret = new ArrayList<>();
    ret.add("-respect-parenthesis");
    {
      ret.add("'('");
      ret.add(inputFile);
      ret.add(options.get(CleaningOptions.ROTATE));
      ret.add(options.get(CleaningOptions.GREYSCALE));
      ret.add(options.get(CleaningOptions.ENHANCE));
      ret.add("')'");
    }
    {
      ret.add("'('");
      ret.add("-clone 0");
      ret.add("-colorspace gray");
      ret.add("-negate");
      ret.add(options.get(CleaningOptions.FILTER));
      ret.add("-contrast-stretch 0");
      ret.add(options.get(CleaningOptions.SMOOTHING_THRESHOLD));
      ret.add("')'");
    }
    ret.add("-compose copy_opacity");
    ret.add("-composite -fill white");
    ret.add("-opaque none");
    ret.add("-alpha off");
    ret.add(options.get(CleaningOptions.UNROTATE));
    ret.add(options.get(CleaningOptions.SHARPEN));
    ret.add(options.get(CleaningOptions.SATURATION));
    ret.add(options.get(CleaningOptions.ADAPTIVE_BLUR));
    ret.add(options.get(CleaningOptions.TRIM));
    ret.add(options.get(CleaningOptions.PAD));
    ret.add(outputFile);
    return CLIUtils.translateCommandline(Joiner.on(" ").join(ret));
  }

  public byte[] convert(String inputFile, String suffix) throws IOException, CommandFailedException {
    File outFile = null;
    try {
      outFile = getTmpOutputFile(suffix);
      if(!new File(inputFile).exists()) {
        throw new FileNotFoundException("Unable to find input file: " + inputFile);
      }
      ArrayList<String> completeCommand = new ArrayList<>();
      {
        String command = "/usr/local/bin/convert";
        if (convertPath.isPresent()) {
          command = convertPath.get();
        }
        completeCommand.add(command);
      }
      for(String s : getCommandLine(inputFile, outFile.getAbsolutePath())) {
        completeCommand.add(s);
      }

      Process p = new ProcessBuilder(completeCommand).start();
      if(p.waitFor() != 0) {
        String stderr = Joiner.on("\n").join(IOUtils.readLines(p.getErrorStream()));
        String stdout = Joiner.on("\n").join(IOUtils.readLines(p.getInputStream()));
        throw new CommandFailedException("Unable to execute convert.  Stderr is: " +  stderr + "\nStdout is: " + stdout);
      }
      return Files.readAllBytes(outFile.toPath());
    } catch (InterruptedException e) {
      throw new CommandFailedException("Unable to complete process!", e);
    } finally {
      if(outFile != null) {
        outFile.delete();
      }
    }
  }

  public EnumMap<CleaningOptions, String> getOptions(int aspectRatio) {
    EnumMap<CleaningOptions, String> ret = new EnumMap(CleaningOptions.class);
    for(CleaningOptions op : CleaningOptions.values()) {
      ret.put(op, op.handler.getIMCommand(aspectRatio, this));
    }
    return ret;
  }

  public static <T extends Enum<T> & Aliased > T getByAlias(String alias, Enum<T> def) {
    for(Enum<T> r : def.getClass().getEnumConstants()) {
       if(((T)r).getAlias().equals(alias)) {
         return (T)r;
       }
    }
    return (T)def;
  }
}
