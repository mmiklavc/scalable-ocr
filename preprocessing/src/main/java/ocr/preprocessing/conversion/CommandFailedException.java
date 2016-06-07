package ocr.preprocessing.conversion;

public class CommandFailedException extends Exception {
  public CommandFailedException(String reason, Throwable t) {
    super(reason, t);
  }
  public CommandFailedException(String reason) {
    super(reason);
  }
}
