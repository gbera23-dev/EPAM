package app.domain.exceptions;

public class TrainingTypeNotFoundException extends RuntimeException {
    public TrainingTypeNotFoundException(String message) {
        super(message);
    }
}
