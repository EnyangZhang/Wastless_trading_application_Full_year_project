package wasteless.exception;

public class UnauthorizedException extends RuntimeException {

  private final String message;

  public UnauthorizedException(String s) {
    this.message = s;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
