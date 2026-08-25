package app.unit.beanPostProcessor;

import app.annotations.PersistenceLayer;
import app.beanPostProcessor.PersistenceLayerBeanPostProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.Advised;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceLayerBeanPostProcessorTest {

    @PersistenceLayer
    interface AnnotatedRepository {
        void save();
    }

    static class AnnotatedRepositoryImpl implements AnnotatedRepository {
        public void save() {
        }
    }

    static class PlainBean {
    }

    @Test
    void TestPostProcessAfterInitializationReturnsSameBeanWhenNotPersistenceLayer() {
        Map<String, String> registry = Map.of("PersistenceLayer", "PERSISTENCE");
        PersistenceLayerBeanPostProcessor processor =
                new PersistenceLayerBeanPostProcessor(registry, 1000L);

        PlainBean bean = new PlainBean();
        Object result = processor.postProcessAfterInitialization(bean, "plainBean");

        assertSame(bean, result);
    }

    @Test
    void TestPostProcessAfterInitializationReturnsProxyWhenPersistenceLayer() {
        Map<String, String> registry = Map.of("PersistenceLayer", "PERSISTENCE");
        PersistenceLayerBeanPostProcessor processor =
                new PersistenceLayerBeanPostProcessor(registry, 1000L);

        AnnotatedRepository bean = new AnnotatedRepositoryImpl();
        Object result = processor.postProcessAfterInitialization(bean, "annotatedRepository");

        assertTrue(result instanceof Advised);
    }
}
