package com.example.Trainer_history_service.beanPostProcessor;

import com.example.Trainer_history_service.methodInterceptors.LoggingMethodInterceptor;
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
public class FacadeLayerBeanPostProcessor implements BeanPostProcessor {
    private final long slowExecutionThresholdMs;
    private final Map<String, String> layerRegistry;

    public FacadeLayerBeanPostProcessor(@Qualifier("LayerRegistry") Map<String, String> layerRegistry,
                                             @Value("${slow-execution-treshold}") long slowExecutionThresholdMs) {
        this.layerRegistry = layerRegistry;
        this.slowExecutionThresholdMs = slowExecutionThresholdMs;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        String packageName = bean.getClass().getPackageName();

        if (!packageName.equals("com.example.Trainer_history_service.facade")) {
            return bean;
        }

        ProxyFactory factory = new ProxyFactory(bean);
        factory.addAdvice(new LoggingMethodInterceptor(layerRegistry.get(packageName),
                slowExecutionThresholdMs));
        return factory.getProxy();
    }
}
