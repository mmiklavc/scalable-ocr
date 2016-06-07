package ocr.preprocessing.conversion.handler;

import ocr.preprocessing.conversion.CleaningOptions;
import ocr.preprocessing.conversion.Handler;
import ocr.preprocessing.conversion.TextCleaner;
import org.apache.commons.cli.CommandLine;

public class AdaptiveBlurringHandler implements Handler {
  @Override
  public void set(CommandLine cli, TextCleaner cleaner) {
    if(CleaningOptions.ADAPTIVE_BLUR.has(cli)) {
      cleaner.setAdaptiveBlur(Integer.parseInt(CleaningOptions.ADAPTIVE_BLUR.get(cli)));
    }
  }

  @Override
  public String getIMCommand(int aspectRatio, TextCleaner cleaner) {
    if(cleaner.getAdaptiveBlur() != 0) {
      return "-adaptive-blur " + cleaner.getAdaptiveBlur();
    }
    return "";
  }
}
