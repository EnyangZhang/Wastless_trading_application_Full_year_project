package wasteless.exception;

public class ConflictException extends RuntimeException {

  private final String message;

  public ConflictException(String s) {
    this.message = s;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
