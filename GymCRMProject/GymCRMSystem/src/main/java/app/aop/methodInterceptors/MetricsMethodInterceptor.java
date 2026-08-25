package app.aop.methodInterceptors;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Objects;

@RequiredArgsConstructor
public class MetricsMethodInterceptor implements MethodInterceptor {

    private final MeterRegistry registry;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String className = Objects.requireNonNull(invocation.getThis()).getClass().getSimpleName();
        String methodName = invocation.getMethod().getName();

        Timer timer = Timer.builder("service.method.duration")
                .tag("class", className)
                .tag("method", methodName)
                .register(registry);

        long start = System.nanoTime();
        try {
            Object result = invocation.proceed();
            registry.counter("service.calls", "method", methodName, "outcome", "success").increment();
            return result;
        } catch (Throwable t) {
            registry.counter("service.calls", "method", methodName, "outcome", "error",
                    "exception", t.getClass().getSimpleName()).increment();
            throw t;
        } finally {
            timer.record(System.nanoTime() - start, java.util.concurrent.TimeUnit.NANOSECONDS);
        }
    }
}