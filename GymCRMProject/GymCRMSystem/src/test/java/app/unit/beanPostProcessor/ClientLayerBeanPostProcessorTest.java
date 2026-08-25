package app.unit.beanPostProcessor;

import app.aop.annotations.ClientLayer;
import app.aop.beanPostProcessor.ClientLayerBeanPostProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.Advised;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientLayerBeanPostProcessorTest {

    @ClientLayer
    static class AnnotatedClient {
        public void call() {
        }
    }

    static class PlainBean {
    }

    @Test
    void TestPostProcessAfterInitializationReturnsSameBeanWhenNotClientLayer() {
        Map<String, String> registry = Map.of("ClientLayer", "CLIENT");
        ClientLayerBeanPostProcessor processor =
                new ClientLayerBeanPostProcessor(registry, 1000L);

        PlainBean bean = new PlainBean();
        Object result = processor.postProcessAfterInitialization(bean, "plainBean");

        assertSame(bean, result);
    }

    @Test
    void TestPostProcessAfterInitializationReturnsProxyWhenClientLayer() {
        Map<String, String> registry = Map.of("ClientLayer", "CLIENT");
        ClientLayerBeanPostProcessor processor =
                new ClientLayerBeanPostProcessor(registry, 1000L);

        AnnotatedClient bean = new AnnotatedClient();
        Object result = processor.postProcessAfterInitialization(bean, "annotatedClient");

        assertTrue(result instanceof Advised);
    }
}
