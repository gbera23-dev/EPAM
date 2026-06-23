package app.exceptions;

public class DDOSProtectionException extends RuntimeException {
    public DDOSProtectionException(String message) {
        super(message);
    }
}
