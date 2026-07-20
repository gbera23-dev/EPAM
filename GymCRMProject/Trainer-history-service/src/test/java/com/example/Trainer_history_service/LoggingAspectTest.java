package com.example.Trainer_history_service;

import com.example.Trainer_history_service.logging.LoggingAspect;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    private LoggingAspect loggingAspect;

    @BeforeEach
    void setUp() {
        loggingAspect = new LoggingAspect();
    }

    @Test
    void testLogRestControllerExecutionReturnsResultWhenAttributesPresent() throws Throwable {
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(attributes.getRequest()).thenReturn(request);
        when(attributes.getResponse()).thenReturn(response);
        when(request.getRequestURI()).thenReturn("/api/trainer");
        when(request.getMethod()).thenReturn("GET");
        when(response.getStatus()).thenReturn(200);
        when(joinPoint.proceed()).thenReturn("result");

        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

            Object result = loggingAspect.logRestControllerExecution(joinPoint);

            assertEquals("result", result);
        }
    }

    @Test
    void testLogRestControllerExecutionProceedsWhenAttributesNull() throws Throwable {
        when(joinPoint.proceed()).thenReturn("result");

        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(null);

            Object result = loggingAspect.logRestControllerExecution(joinPoint);

            assertEquals("result", result);
        }
        verify(joinPoint).proceed();
    }

    @Test
    void testLogRestControllerExecutionProceedsWhenResponseNull() throws Throwable {
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(attributes.getRequest()).thenReturn(request);
        when(attributes.getResponse()).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/trainer");
        when(request.getMethod()).thenReturn("GET");
        when(joinPoint.proceed()).thenReturn("result");

        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

            Object result = loggingAspect.logRestControllerExecution(joinPoint);

            assertEquals("result", result);
        }
        verify(joinPoint, times(2)).proceed();
    }

    @Test
    void testLogRestControllerExecutionThrowsAndLogsWhenExceptionThrown() throws Throwable {
        ServletRequestAttributes attributes = mock(ServletRequestAttributes.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(attributes.getRequest()).thenReturn(request);
        when(request.getRequestURI()).thenReturn("/api/trainer");
        when(request.getMethod()).thenReturn("GET");
        when(joinPoint.proceed()).thenThrow(new RuntimeException("failure"));

        try (MockedStatic<RequestContextHolder> mockedStatic = mockStatic(RequestContextHolder.class)) {
            mockedStatic.when(RequestContextHolder::getRequestAttributes).thenReturn(attributes);

            assertThrows(RuntimeException.class, () -> loggingAspect.logRestControllerExecution(joinPoint));
        }
    }

    @Test
    void testLogServiceExecutionReturnsResult() throws Throwable {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("someMethod");
        when(signature.getDeclaringTypeName()).thenReturn("com.example.SomeService");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenReturn("result");

        Object result = loggingAspect.logServiceExecution(joinPoint);

        assertEquals("result", result);
    }

    @Test
    void testLogServiceExecutionLogsWarnWhenExecutionExceedsThreshold() throws Throwable {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("slowMethod");
        when(signature.getDeclaringTypeName()).thenReturn("com.example.SomeService");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenAnswer(invocation -> {
            Thread.sleep(1005);
            return "result";
        });

        Object result = loggingAspect.logServiceExecution(joinPoint);

        assertEquals("result", result);
    }

    @Test
    void testLogPersistenceExecutionReturnsResult() throws Throwable {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("someRepositoryMethod");
        when(signature.getDeclaringTypeName()).thenReturn("com.example.SomeRepository");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenReturn("result");

        Object result = loggingAspect.logPersistenceExecution(joinPoint);

        assertEquals("result", result);
    }

    @Test
    void testLogPersistenceExecutionLogsWarnWhenExecutionExceedsThreshold() throws Throwable {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("slowRepositoryMethod");
        when(signature.getDeclaringTypeName()).thenReturn("com.example.SomeRepository");
        when(joinPoint.getArgs()).thenReturn(new Object[]{});
        when(joinPoint.proceed()).thenAnswer(invocation -> {
            Thread.sleep(1005);
            return "result";
        });

        Object result = loggingAspect.logPersistenceExecution(joinPoint);

        assertEquals("result", result);
    }

    @Test
    void testLogGlobalErrorLogsExceptionWithoutThrowing() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("com.example.SomeService");
        when(signature.getName()).thenReturn("someMethod");
        Exception exception = new RuntimeException("failure");

        assertDoesNotThrow(() -> loggingAspect.logGlobalError(joinPoint, exception));
    }
}
