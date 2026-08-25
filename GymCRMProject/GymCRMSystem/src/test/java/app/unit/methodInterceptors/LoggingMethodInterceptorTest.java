package app.unit.methodInterceptors;

import app.methodInterceptors.LoggingMethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LoggingMethodInterceptorTest {

    static class Target {
        public void someMethod() {
        }
    }

    @Test
    void TestInvokeReturnsResultOnSuccess() throws Throwable {
        MethodInvocation invocation = mock(MethodInvocation.class);
        Target target = new Target();
        Method method = Target.class.getMethod("someMethod");
        when(invocation.getThis()).thenReturn(target);
        when(invocation.getMethod()).thenReturn(method);
        when(invocation.getArguments()).thenReturn(new Object[0]);
        when(invocation.proceed()).thenReturn("ok");

        LoggingMethodInterceptor interceptor = new LoggingMethodInterceptor("SERVICE", 1000L);
        Object result = interceptor.invoke(invocation);

        assertEquals("ok", result);
    }

    @Test
    void TestInvokeThrowsExceptionOnFailure() throws Throwable {
        MethodInvocation invocation = mock(MethodInvocation.class);
        Target target = new Target();
        Method method = Target.class.getMethod("someMethod");
        when(invocation.getThis()).thenReturn(target);
        when(invocation.getMethod()).thenReturn(method);
        when(invocation.getArguments()).thenReturn(new Object[0]);
        when(invocation.proceed()).thenThrow(new RuntimeException("boom"));

        LoggingMethodInterceptor interceptor = new LoggingMethodInterceptor("SERVICE", 1000L);

        assertThrows(RuntimeException.class, () -> interceptor.invoke(invocation));
    }

    @Test
    void TestInvokeLogsSlowExecution() throws Throwable {
        MethodInvocation invocation = mock(MethodInvocation.class);
        Target target = new Target();
        Method method = Target.class.getMethod("someMethod");
        when(invocation.getThis()).thenReturn(target);
        when(invocation.getMethod()).thenReturn(method);
        when(invocation.getArguments()).thenReturn(new Object[0]);
        when(invocation.proceed()).thenReturn("ok");

        LoggingMethodInterceptor interceptor = new LoggingMethodInterceptor("SERVICE", 0L);
        Object result = interceptor.invoke(invocation);

        assertEquals("ok", result);
    }
}
