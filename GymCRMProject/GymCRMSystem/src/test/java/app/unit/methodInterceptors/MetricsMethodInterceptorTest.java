package app.unit.methodInterceptors;

import app.methodInterceptors.MetricsMethodInterceptor;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class MetricsMethodInterceptorTest {

    static class Target {
        public void someMethod() {
        }
    }

    @Test
    void TestInvokeReturnsResultOnSuccess() throws Throwable {
        MeterRegistry registry = new SimpleMeterRegistry();
        MethodInvocation invocation = mock(MethodInvocation.class);
        Target target = new Target();
        Method method = Target.class.getMethod("someMethod");
        when(invocation.getThis()).thenReturn(target);
        when(invocation.getMethod()).thenReturn(method);
        when(invocation.proceed()).thenReturn("ok");

        MetricsMethodInterceptor interceptor = new MetricsMethodInterceptor(registry);
        Object result = interceptor.invoke(invocation);

        assertEquals("ok", result);
    }

    @Test
    void TestInvokeThrowsExceptionOnFailure() throws Throwable {
        MeterRegistry registry = new SimpleMeterRegistry();
        MethodInvocation invocation = mock(MethodInvocation.class);
        Target target = new Target();
        Method method = Target.class.getMethod("someMethod");
        when(invocation.getThis()).thenReturn(target);
        when(invocation.getMethod()).thenReturn(method);
        when(invocation.proceed()).thenThrow(new RuntimeException("boom"));

        MetricsMethodInterceptor interceptor = new MetricsMethodInterceptor(registry);

        assertThrows(RuntimeException.class, () -> interceptor.invoke(invocation));
    }
}
