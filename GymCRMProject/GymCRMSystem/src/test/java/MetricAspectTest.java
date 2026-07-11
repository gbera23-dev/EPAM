import app.aspects.healthMetrics.MetricsAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MetricsAspectTest {

    @Mock
    private MeterRegistry registry;

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private Signature signature;

    @InjectMocks
    private MetricsAspect aspect;

    private void stubPjpTarget(String className, String methodName) {
        Object target = mock(Object.class);
        doReturn(target).when(pjp).getTarget();
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn(methodName);
    }

    private Timer stubTimer() {
        Timer.Builder builder = mock(Timer.Builder.class);
        Timer timer = mock(Timer.class);
        try (var timerStatic = mockStatic(Timer.class)) {
            timerStatic.when(() -> Timer.builder("service.method.duration")).thenReturn(builder);
            when(builder.tag(anyString(), anyString())).thenReturn(builder);
            when(builder.register(registry)).thenReturn(timer);
        }
        return timer;
    }

    @Test
    void testTimeServiceMethodsProceedReturnsResult() throws Throwable {
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("save");
        Object target = new Object();
        doReturn(target).when(pjp).getTarget();

        Timer.Builder builder = mock(Timer.Builder.class);
        Timer timer = mock(Timer.class);

        try (var timerStatic = mockStatic(Timer.class)) {
            timerStatic.when(() -> Timer.builder("service.method.duration")).thenReturn(builder);
            when(builder.tag(anyString(), anyString())).thenReturn(builder);
            when(builder.register(registry)).thenReturn(timer);
            when(pjp.proceed()).thenReturn("result");

            Object result = aspect.timeServiceMethods(pjp);

            assertEquals("result", result);
            verify(timer).record(anyLong(), any());
        }
    }

    @Test
    void testTimeServiceMethodsProceedThrowsExceptionStillRecords() throws Throwable {
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("save");
        Object target = new Object();
        doReturn(target).when(pjp).getTarget();

        Timer.Builder builder = mock(Timer.Builder.class);
        Timer timer = mock(Timer.class);

        try (var timerStatic = mockStatic(Timer.class)) {
            timerStatic.when(() -> Timer.builder("service.method.duration")).thenReturn(builder);
            when(builder.tag(anyString(), anyString())).thenReturn(builder);
            when(builder.register(registry)).thenReturn(timer);
            when(pjp.proceed()).thenThrow(new RuntimeException("fail"));

            assertThrows(RuntimeException.class, () -> aspect.timeServiceMethods(pjp));
            verify(timer).record(anyLong(), any());
        }
    }

    @Test
    void testCountOutcomesProceedSuccessIncrementsSuccessCounter() throws Throwable {
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("MyService.save()");
        when(pjp.proceed()).thenReturn("ok");

        Counter counter = mock(Counter.class);
        when(registry.counter("service.calls", "method", "MyService.save()", "outcome", "success"))
                .thenReturn(counter);

        Object result = aspect.countOutcomes(pjp);

        assertEquals("ok", result);
        verify(counter).increment();
    }

    @Test
    void testCountOutcomesProceedThrowsIncrementsErrorCounter() throws Throwable {
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("MyService.save()");
        when(pjp.proceed()).thenThrow(new IllegalArgumentException("bad"));

        Counter counter = mock(Counter.class);
        when(registry.counter("service.calls", "method", "MyService.save()", "outcome", "error",
                "exception", "IllegalArgumentException"))
                .thenReturn(counter);

        assertThrows(IllegalArgumentException.class, () -> aspect.countOutcomes(pjp));
        verify(counter).increment();
    }

    @Test
    void testTrackActiveRequestsProceedReturnsResult() throws Throwable {
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("MyController.getAll()");
        when(pjp.proceed()).thenReturn("response");

        AtomicInteger gauge = new AtomicInteger(0);
        when(registry.gauge(eq("http.requests.active"), eq(Tags.of("endpoint", "MyController.getAll()")), any(AtomicInteger.class)))
                .thenReturn(gauge);

        Object result = aspect.trackActiveRequests(pjp);

        assertEquals("response", result);
        assertEquals(0, gauge.get());
    }

    @Test
    void testTrackActiveRequestsProceedThrowsDecrementsGauge() throws Throwable {
        when(pjp.getSignature()).thenReturn(signature);
        when(signature.toShortString()).thenReturn("MyController.getAll()");
        when(pjp.proceed()).thenThrow(new RuntimeException("error"));

        AtomicInteger gauge = new AtomicInteger(0);
        when(registry.gauge(eq("http.requests.active"), eq(Tags.of("endpoint", "MyController.getAll()")), any(AtomicInteger.class)))
                .thenReturn(gauge);

        assertThrows(RuntimeException.class, () -> aspect.trackActiveRequests(pjp));
        assertEquals(0, gauge.get());
    }
}