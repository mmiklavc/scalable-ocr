package ocr.preprocessing.conversion.handler;

import ocr.preprocessing.conversion.CleaningOptions;
import ocr.preprocessing.conversion.Handler;
import ocr.preprocessing.conversion.TextCleaner;
import org.apache.commons.cli.CommandLine;

public class RotationHandler implements Handler {

  @Override
  public void set(CommandLine cli, TextCleaner cleaner) {
    cleaner.setRotate(TextCleaner.getByAlias( CleaningOptions.ROTATE.get(cli)
                                            , TextCleaner.Rotation.getDefault()
                                            )
                     );
  }

  @Override
  public String getIMCommand(int aspectRatio, TextCleaner cleaner) {
    if(cleaner.getLayout() == TextCleaner.Layout.PORTRAIT
            && aspectRatio == 0
            && cleaner.getRotate() == TextCleaner.Rotation.CLOCKWISE
            )
    {
      return "-rotate 90";
    }
    else if(cleaner.getLayout() == TextCleaner.Layout.PORTRAIT
            && aspectRatio == 0
            && cleaner.getRotate() == TextCleaner.Rotation.COUNTERCLOCKWISE
            )
    {
      return "-rotate -90";
    }
    else if(cleaner.getLayout() == TextCleaner.Layout.LANDSCAPE
            && aspectRatio == 1
            && cleaner.getRotate() == TextCleaner.Rotation.CLOCKWISE
            )
    {
      return "-rotate 90";
    }
    else if(cleaner.getLayout() == TextCleaner.Layout.LANDSCAPE
            && aspectRatio == 1
            && cleaner.getRotate() == TextCleaner.Rotation.COUNTERCLOCKWISE
            )
    {
      return "-rotate -90";
    }
    return "";
  }
}
