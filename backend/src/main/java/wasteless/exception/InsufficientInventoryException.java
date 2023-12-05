package wasteless.exception;

/**
 * Exception that should be used when there is insufficient inventory left to decrement it.
 */
public class InsufficientInventoryException extends RuntimeException {
    private final String message;

    public InsufficientInventoryException(String s) {
        this.message = s;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
