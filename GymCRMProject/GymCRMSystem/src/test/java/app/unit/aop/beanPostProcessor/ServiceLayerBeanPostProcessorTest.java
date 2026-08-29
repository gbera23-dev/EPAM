package app.unit.aop.beanPostProcessor;

import app.aop.annotations.ServiceLayer;
import app.aop.beanPostProcessor.ServiceLayerBeanPostProcessor;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.Advised;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceLayerBeanPostProcessorTest {

    @ServiceLayer
    static class AnnotatedService {
        public void doWork() {
        }
    }

    static class PlainBean {
    }

    @Test
    void TestPostProcessAfterInitializationReturnsSameBeanWhenNotServiceLayer() {
        Map<String, String> registry = Map.of("ServiceLayer", "SERVICE");
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        ServiceLayerBeanPostProcessor processor =
                new ServiceLayerBeanPostProcessor(registry, 1000L, meterRegistry);

        PlainBean bean = new PlainBean();
        Object result = processor.postProcessAfterInitialization(bean, "plainBean");

        assertSame(bean, result);
    }

    @Test
    void TestPostProcessAfterInitializationReturnsProxyWhenServiceLayer() {
        Map<String, String> registry = Map.of("ServiceLayer", "SERVICE");
        MeterRegistry meterRegistry = new SimpleMeterRegistry();
        ServiceLayerBeanPostProcessor processor =
                new ServiceLayerBeanPostProcessor(registry, 1000L, meterRegistry);

        AnnotatedService bean = new AnnotatedService();
        Object result = processor.postProcessAfterInitialization(bean, "annotatedService");

        assertTrue(result instanceof Advised);
    }
}
