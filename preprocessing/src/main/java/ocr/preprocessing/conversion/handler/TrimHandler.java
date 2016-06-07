package ocr.preprocessing.conversion.handler;

import ocr.preprocessing.conversion.CleaningOptions;
import ocr.preprocessing.conversion.Handler;
import ocr.preprocessing.conversion.TextCleaner;
import org.apache.commons.cli.CommandLine;

public class TrimHandler implements Handler {
  @Override
  public void set(CommandLine cli, TextCleaner cleaner) {
    cleaner.setTrim(CleaningOptions.TRIM.has(cli));
  }

  @Override
  public String getIMCommand(int aspectRatio, TextCleaner cleaner) {
    if(cleaner.isTrim()) {
      return "-trim +repage";
    }
    return "";
  }
}
