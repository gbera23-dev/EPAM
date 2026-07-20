package com.example.Trainer_history_service;

import com.example.Trainer_history_service.errorHandler.GlobalExceptionHandler;
import com.example.Trainer_history_service.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleUserAuthorizationReturnsForbidden() {
        UserCannotBeAuthorizedException exception = new UserCannotBeAuthorizedException("not authorized");

        ResponseEntity<String> response = handler.handleUserAuthorization(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("not authorized", response.getBody());
    }

    @Test
    void testHandleAlreadyExistingUserReturnsMethodNotAllowed() {
        UserNotLoggedInException exception = new UserNotLoggedInException("not logged in");

        ResponseEntity<String> response = handler.handleAlreadyExistingUser(exception);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertEquals("not logged in", response.getBody());
    }

    @Test
    void testHandleNonExistingUserReturnsNotFound() {
        UserNotFoundException exception = new UserNotFoundException("user not found");

        ResponseEntity<String> response = handler.handleNonExistingUser(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("user not found", response.getBody());
    }

    @Test
    void testHandleNonExistingSessionReturnsNotFound() {
        MonthlySummaryNotFoundException exception = new MonthlySummaryNotFoundException("summary not found");

        ResponseEntity<String> response = handler.handleNonExistingSession(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("summary not found", response.getBody());
    }

    @Test
    void testHandleValidationReturnsBadRequestWithFieldErrorMessage() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "must not be null");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(exception.getStatusCode()).thenReturn(HttpStatusCode.valueOf(400));

        ResponseEntity<Map<String, Object>> response = handler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("must not be null", response.getBody().get("validationMessage"));
    }

    @Test
    void testHandleBadCredentialsReturnsForbidden() {
        BadCredentialsException exception = new BadCredentialsException("bad credentials");

        ResponseEntity<String> response = handler.handleBadCredentials(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("bad credentials", response.getBody());
    }

    @Test
    void testHandleUsernameNotFoundReturnsForbidden() {
        UsernameNotFoundException exception = new UsernameNotFoundException("username not found");

        ResponseEntity<String> response = handler.handleUsernameNotFound(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("username not found", response.getBody());
    }

    @Test
    void testHandleNegativeDurationReturnsNotAcceptable() {
        NegativeDurationException exception = new NegativeDurationException("negative duration");

        ResponseEntity<String> response = handler.handleNegativeDuration(exception);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertEquals("negative duration", response.getBody());
    }

    @Test
    void testHandleGeneralIssueReturnsInternalServerError() {
        Exception exception = new Exception("general failure");

        ResponseEntity<String> response = handler.handleGeneralIssue(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("general failure", response.getBody());
    }
}
