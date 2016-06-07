package ocr.preprocessing.conversion;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.sun.xml.internal.rngom.ast.builder.BuildException;
import ocr.preprocessing.conversion.handler.*;
import org.apache.commons.cli.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.StringTokenizer;
import java.util.function.Function;

public enum CleaningOptions {


   ROTATE("r", s -> Option.builder(s).hasArg()
                                     .longOpt("rotate")
                                     .argName("DIRECTION")
                                     .desc("rotate image 90 degrees in direction specified if aspect ratio does not match layout; options are cw (or clockwise), ccw (or counterclockwise) and n (or none); default=none or no rotation")
                                     .build()
         , new RotationHandler()
         )
  ,LAYOUT("l", s -> Option.builder(s).hasArg()
                                     .longOpt("layout")
                                     .argName("LAYOUT")
                                     .desc("desired layout; options are p (or portrait) or l (or landscape); default=portrait")
                                     .build()
          , new LayoutHandler()
         )
  ,GREYSCALE("g", s -> Option.builder(s)
                                     .longOpt("greyscale")
                                     .desc("convert document to grayscale before enhancing")
                                     .build()
            , new GrayscaleHandler()
            )
  ,ENHANCE("e", s -> Option.builder(s).hasArg()
                                     .longOpt("enhance")
                                     .argName("TYPE")
                                     .desc("enhance image brightness before cleaning; choices are: none, stretch or normalize; default=none")
                                     .build()
          , new EnhancingHandler()
          )
  ,FILTER("f", s -> Option.builder(s).hasArg()
                                     .longOpt("filtersize")
                                     .argName("SIZE")
                                     .type(Integer.class)
                                     .desc("size of filter used to clean background; integer>0; default=15")
                                     .build()
          , new FilterHandler()
          )
  ,OFFSET("o", s -> Option.builder(s).hasArg()
                                     .longOpt("offset")
                                     .argName("SIZE")
                                     .type(Integer.class)
                                     .desc("offset of filter in percent used to reduce noise; integer>=0; default=5")
                                     .build()
          , new OffsetHandler()
          )
  ,UNROTATE("u", s -> Option.builder(s).longOpt("unrotate")
                                     .desc("unrotate image; cannot unrotate more than about 5 degrees")
                                     .build()
          , new UnrotateHandler()
          )
  ,SMOOTHING_THRESHOLD("t", s -> Option.builder(s).hasArg()
                                     .longOpt("threshold")
                                     .argName("THRESHOLD")
                                     .type(Integer.class)
                                     .desc("text smoothing threshold; 0<=threshold<=100; nominal value is about 50; default is no smoothing")
                                     .build()
          , new SmoothingThresholdHandler()
          )
  ,SHARPEN("s", s -> Option.builder(s).hasArg()
                                     .longOpt("sharpamt")
                                     .argName("NUM_PIXELS")
                                     .type(Integer.class)
                                     .desc("sharpening amount in pixels; float>=0; nominal about 1; default=0")
                                     .build()
          , new SharpenHandler()
          )
  ,SATURATION("S", s -> Option.builder(s).hasArg()
                                     .longOpt("saturation")
                                     .argName("SATURATION")
                                     .type(Integer.class)
                                     .desc("color saturation expressed as percent; integer>=0; only applicable if -g not set; a value of 100 is no change; default=200 (double saturation)")
                                     .build()
          , new SaturationHandler()
          )
  ,ADAPTIVE_BLUR("a", s -> Option.builder(s).hasArg()
                                     .longOpt("adaptiveblur")
                                     .argName("BLUR_AMOUNT")
                                     .type(Double.class)
                                     .desc("alternate text smoothing using adaptive blur; floats>=0; default=0 (no smoothing)")
                                     .build()
          , new AdaptiveBlurringHandler()
          )
  ,TRIM("T", s -> Option.builder(s).longOpt("trim")
                                     .desc("trim background around outer part of image ")
                                     .build()
          , new TrimHandler()
          )
  ,PAD("p", s -> Option.builder(s).hasArg()
                                  .longOpt("padamt")
                                  .argName("BLUR_AMOUNT")
                                  .type(Integer.class)
                                  .desc("border pad amount around outer part of image; integer>=0; default=0")
                                  .build()
      , new PadHandler()
      )
  ;
  String shortCode;
  Option option;
  Handler handler;
  CleaningOptions( String shortCode
                 , Function<String, Option> optionCreator
                 , Handler handler
                 )
  {
    this.shortCode = shortCode;
    this.option = optionCreator.apply(shortCode);
    this.handler = handler;
  }

  public boolean has(CommandLine cli) {
    return cli.hasOption(shortCode);
  }

  public String get(CommandLine cli) {
    return cli.getOptionValue(shortCode);
  }

  public static CommandLine parse(CommandLineParser parser, String[] args) {
    try {
      CommandLine cli = parser.parse(getOptions(), args);
      return cli;
    } catch (ParseException e) {
      System.err.println("Unable to parse args: " + Joiner.on(' ').join(args));
      e.printStackTrace(System.err);
      printHelp();
      System.exit(-1);
      return null;
    }
  }

  private static Iterable<Handler> getHandlers() {
    return Iterables.transform(Arrays.asList(values()), x -> x.handler);
  }

  public static TextCleaner createTextCleaner(CommandLine cli, String convertCommand, String outputDir) {
    return new TextCleaner(getHandlers(), cli, convertCommand, outputDir);
  }

  public static TextCleaner createTextCleaner(CommandLine cli, String convertCommand) {
    return createTextCleaner(cli, convertCommand, null);
  }

  public static void printHelp() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "Preprocessor", getOptions());
  }

  public static String getUsage() {
    HelpFormatter formatter = new HelpFormatter();
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    formatter.printOptions(pw, 80, getOptions(), 0, 0);
    pw.flush();
    return sw.toString();
  }

  public static Options getOptions() {
    Options ret = new Options();
    for(CleaningOptions o : CleaningOptions.values()) {
      ret.addOption(o.option);
    }
    return ret;
  }

}
