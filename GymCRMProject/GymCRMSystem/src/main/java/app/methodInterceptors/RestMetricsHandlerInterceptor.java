package app.methodInterceptors;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RestMetricsHandlerInterceptor implements HandlerInterceptor {

    private final MeterRegistry registry;

    public RestMetricsHandlerInterceptor(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String endpoint = request.getRequestURI();

        AtomicInteger active = registry.gauge(
                "http.requests.active",
                Tags.of("endpoint", endpoint),
                new AtomicInteger(0)
        );
        active.incrementAndGet();

        request.setAttribute("activeRequestsGauge", active);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        AtomicInteger active = (AtomicInteger) request.getAttribute("activeRequestsGauge");
        if (active != null) {
            active.decrementAndGet();
        }
    }
}