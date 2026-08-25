package com.example.Trainer_history_service.unit.api.errorHandler;

import com.example.Trainer_history_service.api.errorHandler.GlobalJmsErrorHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class GlobalJmsErrorHandlerTest {

    private GlobalJmsErrorHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalJmsErrorHandler();
    }

    @Test
    void testHandleErrorDoesNotThrowForGivenThrowable() {
        assertDoesNotThrow(() -> handler.handleError(new RuntimeException("boom")));
    }

    @Test
    void testHandleErrorDoesNotThrowWhenMessageIsNull() {
        assertDoesNotThrow(() -> handler.handleError(new RuntimeException()));
    }
}
