package com.example.Trainer_history_service.aop.beanPostProcessor;

import com.example.Trainer_history_service.aop.annotations.FacadeLayer;
import com.example.Trainer_history_service.aop.methodInterceptors.LoggingMethodInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
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

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);

        if (!targetClass.isAnnotationPresent(FacadeLayer.class)) {
            return bean;
        }

        ProxyFactory factory = new ProxyFactory(bean);
        factory.addAdvice(new LoggingMethodInterceptor(layerRegistry.
                get(FacadeLayer.class.getSimpleName()),
                slowExecutionThresholdMs));
        return factory.getProxy();
    }
}
