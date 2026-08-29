package com.example.Trainer_history_service.domain.exceptions;

public class NegativeDurationException extends RuntimeException {
    public NegativeDurationException(String message) {
        super(message);
    }
}
