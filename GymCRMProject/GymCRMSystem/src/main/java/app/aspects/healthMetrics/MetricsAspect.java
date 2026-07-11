package app.aspects.healthMetrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
public class MetricsAspect {

    private final MeterRegistry registry;

    public MetricsAspect(MeterRegistry registry) {
        this.registry = registry;
    }

    @Around("execution(* app.services.*.*(..))")
    public Object timeServiceMethods(ProceedingJoinPoint pjp) throws Throwable {
        String className  = pjp.getTarget().getClass().getSimpleName();
        String methodName = pjp.getSignature().getName();

        Timer timer = Timer.builder("service.method.duration")
                .tag("class",  className)
                .tag("method", methodName)
                .register(registry);

        long start = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            timer.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        }
    }


    @Around("execution(* app.services.*.*(..))")
    public Object countOutcomes(ProceedingJoinPoint pjp) throws Throwable {
        String method = pjp.getSignature().toShortString();
        try {
            Object result = pjp.proceed();
            registry.counter("service.calls", "method", method, "outcome", "success").increment();
            return result;
        } catch (Throwable t) {
            registry.counter("service.calls", "method", method, "outcome", "error",
                    "exception", t.getClass().getSimpleName()).increment();
            throw t;
        }
    }

    @Around("execution(* app.restcontroller.*.*(..))")
    public Object trackActiveRequests(ProceedingJoinPoint pjp) throws Throwable {
        String endpoint = pjp.getSignature().toShortString();
        AtomicInteger active = registry.gauge(
                "http.requests.active",
                Tags.of("endpoint", endpoint),
                new AtomicInteger(0)
        );
        active.incrementAndGet();
        try {
            return pjp.proceed();
        } finally {
            active.decrementAndGet();
        }
    }
}