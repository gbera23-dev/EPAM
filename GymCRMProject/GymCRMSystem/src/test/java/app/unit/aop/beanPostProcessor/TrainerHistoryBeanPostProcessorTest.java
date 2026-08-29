package app.unit.aop.beanPostProcessor;

import app.aop.annotations.InteractsWithTraineeHistoryService;
import app.aop.beanPostProcessor.TrainerHistoryBeanPostProcessor;
import app.messaging.strategies.interfaces.MicroserviceInteractionStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class TrainerHistoryBeanPostProcessorTest {

    static class AnnotatedBean {
        @InteractsWithTraineeHistoryService(chosenStrategy = MicroserviceInteractionStrategy.class)
        public void trackedMethod() {
        }
    }

    static class PlainBean {
        public void plainMethod() {
        }
    }

    @Test
    void TestPostProcessAfterInitializationReturnsSameBeanWhenNoAnnotatedMethod() {
        ApplicationContext context = mock(ApplicationContext.class);
        TrainerHistoryBeanPostProcessor processor = new TrainerHistoryBeanPostProcessor(context);

        PlainBean bean = new PlainBean();
        Object result = processor.postProcessAfterInitialization(bean, "plainBean");

        assertSame(bean, result);
    }

    @Test
    void TestPostProcessAfterInitializationReturnsProxyWhenAnnotatedMethodPresent() {
        ApplicationContext context = mock(ApplicationContext.class);
        TrainerHistoryBeanPostProcessor processor = new TrainerHistoryBeanPostProcessor(context);

        AnnotatedBean bean = new AnnotatedBean();
        Object result = processor.postProcessAfterInitialization(bean, "annotatedBean");

        assertTrue(result instanceof Advised);
    }
}
