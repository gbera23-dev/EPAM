package com.example.Trainer_history_service.api.exceptions;

public class UserCannotBeAuthorizedException extends RuntimeException {
    public UserCannotBeAuthorizedException(String message) {
        super(message);
    }
}
