package com.example.Trainer_history_service.unit.beanPostProcessor;

import com.example.Trainer_history_service.annotations.FacadeLayer;
import com.example.Trainer_history_service.beanPostProcessor.FacadeLayerBeanPostProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.Advised;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FacadeLayerBeanPostProcessorTest {

    @FacadeLayer
    static class AnnotatedFacade {
        public void handle() {
        }
    }

    static class PlainBean {
    }

    @Test
    void TestPostProcessAfterInitializationReturnsSameBeanWhenNotFacadeLayer() {
        Map<String, String> registry = Map.of("FacadeLayer", "FACADE");
        FacadeLayerBeanPostProcessor processor = new FacadeLayerBeanPostProcessor(registry, 1000L);

        PlainBean bean = new PlainBean();
        Object result = processor.postProcessAfterInitialization(bean, "plainBean");

        assertSame(bean, result);
    }

    @Test
    void TestPostProcessAfterInitializationReturnsProxyWhenFacadeLayer() {
        Map<String, String> registry = Map.of("FacadeLayer", "FACADE");
        FacadeLayerBeanPostProcessor processor = new FacadeLayerBeanPostProcessor(registry, 1000L);

        AnnotatedFacade bean = new AnnotatedFacade();
        Object result = processor.postProcessAfterInitialization(bean, "annotatedFacade");

        assertTrue(result instanceof Advised);
    }
}
