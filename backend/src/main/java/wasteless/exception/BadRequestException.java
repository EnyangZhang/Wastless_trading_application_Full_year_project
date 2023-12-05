package wasteless.exception;

public class BadRequestException extends RuntimeException {

  private final String message;

  public BadRequestException(String s) {
    this.message = s;
  }

  @Override
  public String getMessage() {
    return message;
  }
}
