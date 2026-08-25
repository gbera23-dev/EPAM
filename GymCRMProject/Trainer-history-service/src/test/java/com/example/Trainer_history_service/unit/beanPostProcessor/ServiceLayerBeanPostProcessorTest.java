package com.example.Trainer_history_service.unit.beanPostProcessor;

import com.example.Trainer_history_service.annotations.ServiceLayer;
import com.example.Trainer_history_service.beanPostProcessor.ServiceLayerBeanPostProcessor;
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
        ServiceLayerBeanPostProcessor processor = new ServiceLayerBeanPostProcessor(registry, 1000L);

        PlainBean bean = new PlainBean();
        Object result = processor.postProcessAfterInitialization(bean, "plainBean");

        assertSame(bean, result);
    }

    @Test
    void TestPostProcessAfterInitializationReturnsProxyWhenServiceLayer() {
        Map<String, String> registry = Map.of("ServiceLayer", "SERVICE");
        ServiceLayerBeanPostProcessor processor = new ServiceLayerBeanPostProcessor(registry, 1000L);

        AnnotatedService bean = new AnnotatedService();
        Object result = processor.postProcessAfterInitialization(bean, "annotatedService");

        assertTrue(result instanceof Advised);
    }
}
