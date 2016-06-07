package ocr.preprocessing.conversion;

import org.apache.commons.cli.CommandLine;

public interface Handler {
  void set(CommandLine cli, TextCleaner cleaner);
  String getIMCommand(int aspectRatio, TextCleaner cleaner);
}
