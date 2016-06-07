package ocr.preprocessing.conversion.handler;

import ocr.preprocessing.conversion.CleaningOptions;
import ocr.preprocessing.conversion.Handler;
import ocr.preprocessing.conversion.TextCleaner;
import org.apache.commons.cli.CommandLine;

public class LayoutHandler implements Handler{
  @Override
  public void set(CommandLine cli, TextCleaner cleaner) {
    cleaner.setLayout(TextCleaner.getByAlias( CleaningOptions.LAYOUT.get(cli)
                                            , TextCleaner.Layout.getDefault()
                                            )
                     );
  }

  @Override
  public String getIMCommand(int aspectRatio, TextCleaner cleaner) {
    return "";
  }
}
