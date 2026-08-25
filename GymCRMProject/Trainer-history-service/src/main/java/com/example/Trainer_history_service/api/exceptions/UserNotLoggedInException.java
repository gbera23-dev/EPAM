package com.example.Trainer_history_service.api.exceptions;

public class UserNotLoggedInException extends RuntimeException {
    public UserNotLoggedInException(String message) {
        super(message);
    }
}
