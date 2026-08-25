package app.beanPostProcessor;

import app.annotations.PersistenceLayer;
import app.methodInterceptors.LoggingMethodInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class PersistenceLayerBeanPostProcessor implements BeanPostProcessor {

    private final long slowExecutionThresholdMs;
    private final Map<String, String> layerRegistry;

    public PersistenceLayerBeanPostProcessor(@Qualifier("LayerRegistry") Map<String, String> layerRegistry,
                                             @Value("${slow-execution-treshold}") long slowExecutionThresholdMs) {
        this.layerRegistry = layerRegistry;
        this.slowExecutionThresholdMs = slowExecutionThresholdMs;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (!isPersistenceLayerBean(bean)) {
            return bean;
        }

        ProxyFactory factory = new ProxyFactory(bean);
        factory.addAdvice(new LoggingMethodInterceptor
                (layerRegistry.get(PersistenceLayer.class.getSimpleName()),
                slowExecutionThresholdMs));
        return factory.getProxy();
    }

    private boolean isPersistenceLayerBean(Object bean) {
        for (Class<?> iface : bean.getClass().getInterfaces()) {
            if (iface.isAnnotationPresent(PersistenceLayer.class)) {
                return true;
            }
        }
        return false;
    }
}