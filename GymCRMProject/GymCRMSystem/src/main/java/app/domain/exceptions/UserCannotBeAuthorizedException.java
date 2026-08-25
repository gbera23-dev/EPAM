package app.domain.exceptions;

public class UserCannotBeAuthorizedException extends RuntimeException {
    public UserCannotBeAuthorizedException(String message) {
        super(message);
    }
}
