package app.domain.exceptions;

public class TrainingNotFoundException extends RuntimeException {
    public TrainingNotFoundException(String message) {
        super(message);
    }
}
