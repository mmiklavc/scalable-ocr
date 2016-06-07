package ocr.preprocessing.conversion.handler;

import ocr.preprocessing.conversion.CleaningOptions;
import ocr.preprocessing.conversion.Handler;
import ocr.preprocessing.conversion.TextCleaner;
import org.apache.commons.cli.CommandLine;

import java.util.Optional;

public class SmoothingThresholdHandler implements Handler {



  @Override
  public void set(CommandLine cli, TextCleaner cleaner) {
    if(CleaningOptions.SMOOTHING_THRESHOLD.has(cli)) {
      cleaner.setThreshold(Optional.of(Integer.parseInt(CleaningOptions.SMOOTHING_THRESHOLD.get(cli))));
    }
  }

  @Override
  public String getIMCommand(int aspectRatio, TextCleaner cleaner) {
    if(cleaner.getThreshold().isPresent()) {
      return "-blur 1x65535 -level " + cleaner.getThreshold().get() + "x100%";
    }
    return "";
  }
}
