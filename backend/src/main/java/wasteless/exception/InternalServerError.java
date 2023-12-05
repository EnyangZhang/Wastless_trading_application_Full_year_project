package wasteless.exception;

public class InternalServerError extends RuntimeException {
    private final String message;

    public InternalServerError(String s) {
        this.message = s;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
