package ocr.preprocessing.conversion.handler;

import ocr.preprocessing.conversion.CleaningOptions;
import ocr.preprocessing.conversion.Handler;
import ocr.preprocessing.conversion.TextCleaner;
import org.apache.commons.cli.CommandLine;

public class FilterHandler implements Handler {
  @Override
  public void set(CommandLine cli, TextCleaner cleaner) {

    if(CleaningOptions.FILTER.has(cli)) {
      cleaner.setFilterSize(Integer.parseInt(CleaningOptions.FILTER.get(cli)));
    }
  }

  @Override
  public String getIMCommand(int aspectRatio, TextCleaner cleaner) {
    return "-lat " + cleaner.getFilterSize() + "x" + cleaner.getFilterSize() + "+" + cleaner.getOffset() + "%";
  }
}
