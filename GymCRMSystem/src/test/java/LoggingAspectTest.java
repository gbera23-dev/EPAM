
import app.logging.LoggingAspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private Signature signature;

    @BeforeEach
    void setUp() {
        when(signature.getName()).thenReturn("testMethod");
        when(signature.getDeclaringTypeName()).thenReturn("com.example.TestClass");
    }

    @Test
    void testLogServiceExecutionReturnsResultFromProceed() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenReturn("expectedResult");

        Object result = loggingAspect.logServiceExecution(proceedingJoinPoint);

        assertEquals("expectedResult", result);
    }

    @Test
    void testLogServiceExecutionCallsProceed() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenReturn(null);

        loggingAspect.logServiceExecution(proceedingJoinPoint);

        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    void testLogServiceExecutionWithNullArgs() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(null);
        when(proceedingJoinPoint.proceed()).thenReturn(null);

        assertDoesNotThrow(() -> loggingAspect.logServiceExecution(proceedingJoinPoint));
    }

    @Test
    void testLogServiceExecutionWithEmptyArgs() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenReturn(null);

        assertDoesNotThrow(() -> loggingAspect.logServiceExecution(proceedingJoinPoint));
    }

    @Test
    void testLogServiceExecutionWithMultipleArgs() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{"arg1", 42, true});
        when(proceedingJoinPoint.proceed()).thenReturn(null);

        assertDoesNotThrow(() -> loggingAspect.logServiceExecution(proceedingJoinPoint));
    }

    @Test
    void testLogServiceExecutionWithNullArgInArray() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{null, "value"});
        when(proceedingJoinPoint.proceed()).thenReturn(null);

        assertDoesNotThrow(() -> loggingAspect.logServiceExecution(proceedingJoinPoint));
    }

    @Test
    void testLogServiceExecutionPropagatesException() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenThrow(new RuntimeException("service error"));

        assertThrows(RuntimeException.class, () -> loggingAspect.logServiceExecution(proceedingJoinPoint));
    }

    @Test
    void testLogServiceExecutionReturnsNullWhenProceedReturnsNull() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenReturn(null);

        Object result = loggingAspect.logServiceExecution(proceedingJoinPoint);

        assertNull(result);
    }

    @Test
    void testLogPersistenceExecutionReturnsResultFromProceed() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenReturn("daoResult");

        Object result = loggingAspect.logPersistenceExecution(proceedingJoinPoint);

        assertEquals("daoResult", result);
    }

    @Test
    void testLogPersistenceExecutionCallsProceed() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenReturn(null);

        loggingAspect.logPersistenceExecution(proceedingJoinPoint);

        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    void testLogPersistenceExecutionWithNullArgs() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(null);
        when(proceedingJoinPoint.proceed()).thenReturn(null);

        assertDoesNotThrow(() -> loggingAspect.logPersistenceExecution(proceedingJoinPoint));
    }

    @Test
    void testLogPersistenceExecutionWithEmptyArgs() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenReturn(null);

        assertDoesNotThrow(() -> loggingAspect.logPersistenceExecution(proceedingJoinPoint));
    }

    @Test
    void testLogPersistenceExecutionWithMultipleArgs() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{1L, "entityName"});
        when(proceedingJoinPoint.proceed()).thenReturn(null);

        assertDoesNotThrow(() -> loggingAspect.logPersistenceExecution(proceedingJoinPoint));
    }

    @Test
    void testLogPersistenceExecutionPropagatesException() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenThrow(new IllegalArgumentException("dao error"));

        assertThrows(IllegalArgumentException.class, () -> loggingAspect.logPersistenceExecution(proceedingJoinPoint));
    }

    @Test
    void testLogPersistenceExecutionReturnsNullWhenProceedReturnsNull() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenReturn(null);

        Object result = loggingAspect.logPersistenceExecution(proceedingJoinPoint);

        assertNull(result);
    }

    @Test
    void testLogGlobalErrorDoesNotThrow() {
        when(joinPoint.getSignature()).thenReturn(signature);
        Exception ex = new RuntimeException("something went wrong");

        assertDoesNotThrow(() -> loggingAspect.logGlobalError(joinPoint, ex));
    }

    @Test
    void testLogGlobalErrorHandlesExceptionWithNullMessage() {
        when(joinPoint.getSignature()).thenReturn(signature);
        Exception ex = new RuntimeException((String) null);

        assertDoesNotThrow(() -> loggingAspect.logGlobalError(joinPoint, ex));
    }

    @Test
    void testLogGlobalErrorHandlesCheckedExceptions() {
        when(joinPoint.getSignature()).thenReturn(signature);
        Exception ex = new Exception("checked exception");

        assertDoesNotThrow(() -> loggingAspect.logGlobalError(joinPoint, ex));
    }

    @Test
    void testLogGlobalErrorHandlesIllegalArgumentException() {
        when(joinPoint.getSignature()).thenReturn(signature);
        Exception ex = new IllegalArgumentException("bad input");

        assertDoesNotThrow(() -> loggingAspect.logGlobalError(joinPoint, ex));
    }

    @Test
    void testLogServiceExecutionSlowMethodDoesNotThrow() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenAnswer(invocation -> {
            Thread.sleep(1001);
            return "slowResult";
        });

        assertDoesNotThrow(() -> loggingAspect.logServiceExecution(proceedingJoinPoint));
    }

    @Test
    void testLogPersistenceExecutionSlowMethodDoesNotThrow() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{});
        when(proceedingJoinPoint.proceed()).thenAnswer(invocation -> {
            Thread.sleep(1001);
            return null;
        });

        assertDoesNotThrow(() -> loggingAspect.logPersistenceExecution(proceedingJoinPoint));
    }
}