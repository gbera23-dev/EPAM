package com.example.Trainer_history_service.exceptions;

public class NegativeDurationException extends RuntimeException {
    public NegativeDurationException(String message) {
        super(message);
    }
}
