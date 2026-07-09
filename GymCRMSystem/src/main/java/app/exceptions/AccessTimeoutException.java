package app.exceptions;

public class AccessTimeoutException extends RuntimeException {
    public AccessTimeoutException(String message) {
        super(message);
    }
}
