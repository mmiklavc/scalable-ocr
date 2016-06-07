package ocr.preprocessing.conversion.handler;

import ocr.preprocessing.conversion.CleaningOptions;
import ocr.preprocessing.conversion.Handler;
import ocr.preprocessing.conversion.TextCleaner;
import org.apache.commons.cli.CommandLine;

public class GrayscaleHandler implements Handler {
  @Override
  public void set(CommandLine cli, TextCleaner cleaner) {
    cleaner.setGrayscale(CleaningOptions.GREYSCALE.has(cli));
  }

  @Override
  public String getIMCommand(int aspectRatio, TextCleaner cleaner) {
    if(cleaner.isGrayscale()) {
      return "-colorspace gray -type grayscale";
    }
    return "";
  }
}
