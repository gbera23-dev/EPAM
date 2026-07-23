package com.example.Trainer_history_service;

import com.example.Trainer_history_service.aspects.LoggingAspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @Mock private ProceedingJoinPoint pjp;
    @Mock private JoinPoint joinPoint;
    @Mock private Signature signature;

    private LoggingAspect loggingAspect;

    @BeforeEach
    void setUp() {
        loggingAspect = new LoggingAspect();
        lenient().when(pjp.getSignature()).thenReturn(signature);
        lenient().when(joinPoint.getSignature()).thenReturn(signature);
        lenient().when(signature.getName()).thenReturn("someMethod");
        lenient().when(signature.getDeclaringTypeName()).thenReturn("com.example.Trainer_history_service.SomeClass");
    }

    @Test
    void testLogFacadeLayerExecutionReturnsProceedResult() throws Throwable {
        when(pjp.proceed()).thenReturn("facade-result");

        Object result = loggingAspect.logFacadeLayerExecution(pjp);

        assertEquals("facade-result", result);
        verify(pjp).proceed();
    }

    @Test
    void testLogFacadeLayerExecutionPropagatesExceptionFromProceed() throws Throwable {
        when(pjp.proceed()).thenThrow(new IllegalStateException("failure"));

        assertThrows(IllegalStateException.class, () -> loggingAspect.logFacadeLayerExecution(pjp));
    }

    @Test
    void testLogServiceExecutionReturnsProceedResult() throws Throwable {
        when(pjp.proceed()).thenReturn("service-result");

        Object result = loggingAspect.logServiceExecution(pjp);

        assertEquals("service-result", result);
        verify(pjp).proceed();
    }

    @Test
    void testLogPersistenceExecutionReturnsProceedResult() throws Throwable {
        when(pjp.proceed()).thenReturn("repo-result");

        Object result = loggingAspect.logPersistenceExecution(pjp);

        assertEquals("repo-result", result);
        verify(pjp).proceed();
    }

    @Test
    void testLogGlobalErrorDoesNotThrow() {
        assertDoesNotThrow(() -> loggingAspect.logGlobalError(joinPoint, new RuntimeException("boom")));
    }
}
