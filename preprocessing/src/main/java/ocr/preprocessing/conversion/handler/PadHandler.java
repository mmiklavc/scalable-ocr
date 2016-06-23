package ocr.preprocessing.conversion.handler;

import ocr.preprocessing.conversion.CleaningOptions;
import ocr.preprocessing.conversion.Handler;
import ocr.preprocessing.conversion.TextCleaner;
import org.apache.commons.cli.CommandLine;

public class PadHandler implements Handler {

  @Override
  public void set(CommandLine cli, TextCleaner cleaner) {

    if(CleaningOptions.PAD.has(cli)) {
      cleaner.setPadAmt(Integer.parseInt(CleaningOptions.PAD.get(cli)));
    }
  }

  @Override
  public String getIMCommand(int aspectRatio, TextCleaner cleaner) {
    if(cleaner.getPadAmt() > 0) {
      return "-compose over -bordercolor white -border " + cleaner.getPadAmt();
    }
    return "";
  }
}
