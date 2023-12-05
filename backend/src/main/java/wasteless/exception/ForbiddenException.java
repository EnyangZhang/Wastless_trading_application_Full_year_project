package wasteless.exception;

public class ForbiddenException extends RuntimeException {

  private final String message;

  public ForbiddenException(String s) {
    this.message = s;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
