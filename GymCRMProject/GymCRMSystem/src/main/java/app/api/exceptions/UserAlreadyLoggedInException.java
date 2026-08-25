package app.api.exceptions;

public class UserAlreadyLoggedInException extends RuntimeException {
    public UserAlreadyLoggedInException(String message) {
        super(message);
    }
}
