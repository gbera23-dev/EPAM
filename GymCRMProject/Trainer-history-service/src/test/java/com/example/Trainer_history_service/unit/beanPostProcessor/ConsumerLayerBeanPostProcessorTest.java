package com.example.Trainer_history_service.unit.beanPostProcessor;

import com.example.Trainer_history_service.aop.annotations.ConsumerLayer;
import com.example.Trainer_history_service.aop.beanPostProcessor.ConsumerLayerBeanPostProcessor;
import com.example.Trainer_history_service.services.security.interfaces.JWTService;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.Advised;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ConsumerLayerBeanPostProcessorTest {

    @ConsumerLayer
    static class AnnotatedConsumer {
        public void consume() {
        }
    }

    static class PlainBean {
    }

    @Test
    void TestPostProcessAfterInitializationReturnsSameBeanWhenNotConsumerLayer() {
        JWTService jwtService = mock(JWTService.class);
        ConsumerLayerBeanPostProcessor processor = new ConsumerLayerBeanPostProcessor(jwtService);

        PlainBean bean = new PlainBean();
        Object result = processor.postProcessAfterInitialization(bean, "plainBean");

        assertSame(bean, result);
    }

    @Test
    void TestPostProcessAfterInitializationReturnsProxyWhenConsumerLayer() {
        JWTService jwtService = mock(JWTService.class);
        ConsumerLayerBeanPostProcessor processor = new ConsumerLayerBeanPostProcessor(jwtService);

        AnnotatedConsumer bean = new AnnotatedConsumer();
        Object result = processor.postProcessAfterInitialization(bean, "annotatedConsumer");

        assertTrue(result instanceof Advised);
    }
}
