import app.errorHandler.GlobalExceptionHandler;
import app.exceptions.*;
import jakarta.validation.UnexpectedTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {

    GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setup() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    public void UserCannotBeAuthorizedExceptionTest() {
        UserCannotBeAuthorizedException e = new UserCannotBeAuthorizedException("cannot authorize user");

        ResponseEntity<String> result = globalExceptionHandler.handleUserAuthorization(e);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertEquals(e.getMessage(), result.getBody());
    }

    @Test
    public void UserNotLoggedInExceptionTest() {
        UserNotLoggedInException e = new UserNotLoggedInException("cannot log in user");

        ResponseEntity<String> result = globalExceptionHandler.handleAlreadyExistingUser(e);

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, result.getStatusCode());
        assertEquals(e.getMessage(), result.getBody());
    }

    @Test
    public void UserNotFoundExceptionTest() {
        UserNotFoundException e = new UserNotFoundException("user not found");

        ResponseEntity<String> result = globalExceptionHandler.handleNonExistingUser(e);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals(e.getMessage(), result.getBody());
    }

    @Test
    public void SessionNotFoundExceptionTest() {
        SessionNotFoundException e = new SessionNotFoundException("session not found");

        ResponseEntity<String> result = globalExceptionHandler.handleNonExistingSession(e);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals(e.getMessage(), result.getBody());
    }

    @Test
    public void UserAlreadyActiveExceptionTest() {
        UserAlreadyActiveException e = new UserAlreadyActiveException("user already active");

        ResponseEntity<String> result = globalExceptionHandler.handleAlreadyActiveUser(e);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, result.getStatusCode());
        assertEquals(e.getMessage(), result.getBody());
    }

    @Test
    public void PasswordDoesNotMatchExceptionTest() {
        PasswordDoesNotMatchException e = new PasswordDoesNotMatchException("password does not match");

        ResponseEntity<String> result = globalExceptionHandler.handleIncorrectPassword(e);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertEquals(e.getMessage(), result.getBody());
    }

    @Test
    public void UserAlreadyLoggedInExceptionTest() {
        UserAlreadyLoggedInException e = new UserAlreadyLoggedInException("user already logged in");

        ResponseEntity<String> result = globalExceptionHandler.handleAlreadyLoggedInUser(e);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, result.getStatusCode());
        assertEquals(e.getMessage(), result.getBody());
    }

    @Test
    public void UserAlreadyInactiveExceptionTest() {
        UserAlreadyInactiveException e = new UserAlreadyInactiveException("user already inactive");

        ResponseEntity<String> result = globalExceptionHandler.handleAlreadyActiveUser(e);

        assertEquals(HttpStatus.NOT_ACCEPTABLE, result.getStatusCode());
        assertEquals(e.getMessage(), result.getBody());
    }

    @Test
    public void MethodArgumentNotValidExceptionTest() {
        MethodArgumentNotValidException e = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "validation failed");

        when(e.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(e.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleValidation(e);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("validation failed", result.getBody().get("validationMessage"));
        assertEquals(HttpStatus.BAD_REQUEST, result.getBody().get("statusMessage"));
    }

    @Test
    public void UnexpectedTypeExceptionTest() {
        UnexpectedTypeException e = new UnexpectedTypeException("unexpected type error");

        ResponseEntity<Map<String, Object>> result = globalExceptionHandler.handleUnexpectedType(e);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals(e.getLocalizedMessage(), result.getBody().get("validationMessage"));
    }

    @Test
    public void GeneralExceptionTest() {
        Exception e = new Exception("internal server error");

        ResponseEntity<String> result = globalExceptionHandler.handleGeneralIssue(e);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals(e.getMessage(), result.getBody());
    }
}