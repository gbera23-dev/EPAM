package app.unit.methodInterceptors;

import app.methodInterceptors.RestMetricsHandlerInterceptor;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class RestMetricsHandlerInterceptorTest {

    private MeterRegistry registry;
    private RestMetricsHandlerInterceptor interceptor;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        interceptor = new RestMetricsHandlerInterceptor(registry);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void TestPreHandleReturnsTrue() {
        boolean result = interceptor.preHandle(request, response, new Object());
        assertTrue(result);
    }

    @Test
    void TestPreHandleIncrementsActiveGauge() {
        interceptor.preHandle(request, response, new Object());
        verify(request).setAttribute(eq("activeRequestsGauge"), any(AtomicInteger.class));
    }

    @Test
    void TestAfterCompletionDecrementsActiveGauge() {
        AtomicInteger active = new AtomicInteger(1);
        when(request.getAttribute("activeRequestsGauge")).thenReturn(active);
        interceptor.afterCompletion(request, response, new Object(), null);
        assertEquals(0, active.get());
    }

    @Test
    void TestAfterCompletionHandlesNullGauge() {
        when(request.getAttribute("activeRequestsGauge")).thenReturn(null);
        interceptor.afterCompletion(request, response, new Object(), null);
    }
}
