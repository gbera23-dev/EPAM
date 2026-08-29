package app.aop.beanPostProcessor;

import app.aop.annotations.ClientLayer;
import app.aop.methodInterceptors.LoggingMethodInterceptor;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ClientLayerBeanPostProcessor implements BeanPostProcessor {

    private final long slowExecutionThresholdMs;
    private final Map<String, String> layerRegistry;

    public ClientLayerBeanPostProcessor(@Qualifier("LayerRegistry") Map<String, String> layerRegistry,
                                        @Value("${slow-execution-treshold}") long slowExecutionThresholdMs) {
        this.layerRegistry = layerRegistry;
        this.slowExecutionThresholdMs = slowExecutionThresholdMs;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);

        if(!targetClass.isAnnotationPresent(ClientLayer.class)) {
            return bean;
        }

        ProxyFactory factory = new ProxyFactory(bean);
        factory.addAdvice(new LoggingMethodInterceptor(layerRegistry.get(ClientLayer.class.getSimpleName()),
                slowExecutionThresholdMs));
        return factory.getProxy();
    }
}