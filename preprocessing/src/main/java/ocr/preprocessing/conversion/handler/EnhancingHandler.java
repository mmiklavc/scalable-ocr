package ocr.preprocessing.conversion.handler;

import ocr.preprocessing.conversion.CleaningOptions;
import ocr.preprocessing.conversion.Handler;
import ocr.preprocessing.conversion.TextCleaner;
import org.apache.commons.cli.CommandLine;

public class EnhancingHandler implements Handler {

  @Override
  public void set(CommandLine cli, TextCleaner cleaner) {
  cleaner.setEnhance(TextCleaner.getByAlias( CleaningOptions.ENHANCE.get(cli)
                                            , TextCleaner.Enhance.getDefault()
                                            )
                     );
  }

  @Override
  public String getIMCommand(int aspectRatio, TextCleaner cleaner) {
    if(cleaner.getEnhance() == TextCleaner.Enhance.STRETCH) {
      return "-contrast-stretch 0";
    }
    else if(cleaner.getEnhance() == TextCleaner.Enhance.STRETCH) {
      return "-normalize 0";
    }
    return "";
  }
}
