package ocr.preprocessing.conversion.handler;

import ocr.preprocessing.conversion.CleaningOptions;
import ocr.preprocessing.conversion.Handler;
import ocr.preprocessing.conversion.TextCleaner;
import org.apache.commons.cli.CommandLine;

public class SaturationHandler implements Handler {
  @Override
  public void set(CommandLine cli, TextCleaner cleaner) {
     if(CleaningOptions.SATURATION.has(cli)) {
      String saturation= CleaningOptions.SATURATION.get(cli);
      cleaner.setSaturation((int)Double.parseDouble(saturation));
    }
    if(CleaningOptions.GREYSCALE.has(cli)) {
      cleaner.setSaturation(100);
    }
  }

  @Override
  public String getIMCommand(int aspectRatio, TextCleaner cleaner) {
    if(cleaner.getSaturation() == 100) {
      return "";
    }
    else {
      return "-modulate 100," + cleaner.getSaturation() + ",100";
    }
  }
}
