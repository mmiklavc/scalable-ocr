package ocr.preprocessing.conversion.handler;

import ocr.preprocessing.conversion.CleaningOptions;
import ocr.preprocessing.conversion.Handler;
import ocr.preprocessing.conversion.TextCleaner;
import org.apache.commons.cli.CommandLine;

public class OffsetHandler implements Handler {
  @Override
  public void set(CommandLine cli, TextCleaner cleaner) {
   if(CleaningOptions.OFFSET.has(cli)) {
      cleaner.setOffset(Integer.parseInt(CleaningOptions.OFFSET.get(cli)));
    }
  }

  @Override
  public String getIMCommand(int aspectRatio, TextCleaner cleaner) {
    return "";
  }
}
