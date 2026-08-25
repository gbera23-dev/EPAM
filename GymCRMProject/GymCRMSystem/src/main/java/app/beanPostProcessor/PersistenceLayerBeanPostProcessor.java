package app.beanPostProcessor;

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
        String packageName = "app.persistence";

        if (!isPersistenceLayerBean(bean)) {
            return bean;
        }

        log.info("Persistence bean class: {} package:  {}", bean.getClass(), packageName);

        ProxyFactory factory = new ProxyFactory(bean);
        factory.addAdvice(new LoggingMethodInterceptor(layerRegistry.get(packageName),
                slowExecutionThresholdMs));
        return factory.getProxy();
    }

    private boolean isPersistenceLayerBean(Object bean) {
        if (bean.getClass().getPackageName().equals("app.persistence")) {
            return true;
        }
        for (Class<?> iface : bean.getClass().getInterfaces()) {
            if (iface.getPackageName().equals("app.persistence")) {
                return true;
            }
        }
        return false;
    }
}