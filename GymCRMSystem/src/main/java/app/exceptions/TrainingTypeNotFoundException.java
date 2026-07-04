package app.exceptions;

public class TrainingTypeNotFoundException extends RuntimeException {
    public TrainingTypeNotFoundException(String message) {
        super(message);
    }
}
