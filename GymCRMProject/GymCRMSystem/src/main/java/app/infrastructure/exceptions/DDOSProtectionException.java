package app.infrastructure.exceptions;

public class DDOSProtectionException extends RuntimeException {
    public DDOSProtectionException(String message) {
        super(message);
    }
}
