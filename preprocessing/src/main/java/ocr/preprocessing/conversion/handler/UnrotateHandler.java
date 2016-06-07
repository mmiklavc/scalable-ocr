package ocr.preprocessing.conversion.handler;

import ocr.preprocessing.conversion.CleaningOptions;
import ocr.preprocessing.conversion.Handler;
import ocr.preprocessing.conversion.TextCleaner;
import org.apache.commons.cli.CommandLine;

public class UnrotateHandler implements Handler {
  @Override
  public void set(CommandLine cli, TextCleaner cleaner) {
    cleaner.setUnrotate(CleaningOptions.UNROTATE.has(cli));
  }

  @Override
  public String getIMCommand(int aspectRatio, TextCleaner cleaner) {
    if(cleaner.isUnrotate()) {
      return "-background white -deskew 40%";
    }
    return "";
  }
}
