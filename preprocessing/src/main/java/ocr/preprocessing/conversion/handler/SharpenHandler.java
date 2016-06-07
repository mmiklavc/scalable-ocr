package ocr.preprocessing.conversion.handler;

import ocr.preprocessing.conversion.CleaningOptions;
import ocr.preprocessing.conversion.Handler;
import ocr.preprocessing.conversion.TextCleaner;
import org.apache.commons.cli.CommandLine;

public class SharpenHandler implements Handler {
  @Override
  public void set(CommandLine cli, TextCleaner cleaner) {
    if(CleaningOptions.SHARPEN.has(cli)) {
      String sharpenAmt = CleaningOptions.SHARPEN.get(cli);
      cleaner.setSharpAmt((int)Double.parseDouble(sharpenAmt));
    }
  }

  @Override
  public String getIMCommand(int aspectRatio, TextCleaner cleaner) {
    if(cleaner.getSharpAmt() > 0) {
      return "-sharpen 0x" + cleaner.getSharpAmt();
    }
    return "";
  }
}
