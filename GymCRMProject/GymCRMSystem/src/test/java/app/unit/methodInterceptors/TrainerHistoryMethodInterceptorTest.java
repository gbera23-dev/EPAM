package app.unit.methodInterceptors;

import app.annotations.InteractsWithTraineeHistoryService;
import app.methodInterceptors.TrainerHistoryMethodInterceptor;
import app.strategies.MicroserviceInteraction.MicroserviceInteractionStrategy;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TrainerHistoryMethodInterceptorTest {

    static class NoAnnotationTarget {
        public void plainMethod() {
        }
    }

    static class AnnotatedTarget {
        @InteractsWithTraineeHistoryService(chosenStrategy = MicroserviceInteractionStrategy.class)
        public void annotatedMethod() {
        }
    }

    @Test
    void TestInvokeProceedsWhenAnnotationAbsent() throws Throwable {
        ApplicationContext context = mock(ApplicationContext.class);
        MethodInvocation invocation = mock(MethodInvocation.class);
        Method method = NoAnnotationTarget.class.getMethod("plainMethod");
        when(invocation.getMethod()).thenReturn(method);
        when(invocation.proceed()).thenReturn("proceeded");

        TrainerHistoryMethodInterceptor interceptor = new TrainerHistoryMethodInterceptor(context);
        Object result = interceptor.invoke(invocation);

        assertEquals("proceeded", result);
        verify(invocation).proceed();
    }

    @Test
    void TestInvokeCallsStrategyWhenAnnotationPresent() throws Throwable {
        ApplicationContext context = mock(ApplicationContext.class);
        MethodInvocation invocation = mock(MethodInvocation.class);
        Method method = AnnotatedTarget.class.getMethod("annotatedMethod");
        when(invocation.getMethod()).thenReturn(method);

        MicroserviceInteractionStrategy strategy = mock(MicroserviceInteractionStrategy.class);
        when(context.getBean(MicroserviceInteractionStrategy.class)).thenReturn(strategy);
        when(strategy.sendTheRequest(invocation)).thenReturn("strategyResult");

        TrainerHistoryMethodInterceptor interceptor = new TrainerHistoryMethodInterceptor(context);
        Object result = interceptor.invoke(invocation);

        assertEquals("strategyResult", result);
        verify(strategy).sendTheRequest(invocation);
    }
}
