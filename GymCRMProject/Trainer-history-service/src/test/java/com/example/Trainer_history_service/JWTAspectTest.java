package com.example.Trainer_history_service;

import com.example.Trainer_history_service.aspects.JWTAspect;
import com.example.Trainer_history_service.exceptions.UserCannotBeAuthorizedException;
import com.example.Trainer_history_service.services.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTAspectTest {

    @Mock private JWTService jwtService;

    private JWTAspect jwtAspect;
    private Method validateJWTToken;

    @BeforeEach
    void setUp() throws Exception {
        jwtAspect = new JWTAspect(jwtService);
        validateJWTToken = JWTAspect.class.getDeclaredMethod("validateJWTToken", String.class, String.class);
        validateJWTToken.setAccessible(true);
    }

    @Test
    void testValidateJWTTokenStripsBearerPrefixBeforeValidating() throws Exception {
        when(jwtService.tokenIsValid("raw-token")).thenReturn(true);

        validateJWTToken.invoke(jwtAspect, "Bearer raw-token", "txn-1");

        verify(jwtService).tokenIsValid("raw-token");
    }

    @Test
    void testValidateJWTTokenDoesNotPropagateAuthorizationException() throws Exception {
        when(jwtService.tokenIsValid("bad-token")).thenThrow(new UserCannotBeAuthorizedException("invalid token"));

        assertDoesNotThrow(() -> validateJWTToken.invoke(jwtAspect, "Bearer bad-token", "txn-1"));
    }
}
