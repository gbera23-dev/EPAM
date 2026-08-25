package app.api.exceptions;

public class AccessTimeoutException extends RuntimeException {
    public AccessTimeoutException(String message) {
        super(message);
    }
}
