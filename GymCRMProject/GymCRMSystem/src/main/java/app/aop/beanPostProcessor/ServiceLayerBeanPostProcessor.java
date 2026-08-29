package app.aop.beanPostProcessor;

import app.aop.annotations.ServiceLayer;
import app.aop.methodInterceptors.LoggingMethodInterceptor;
import app.aop.methodInterceptors.MetricsMethodInterceptor;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ServiceLayerBeanPostProcessor implements BeanPostProcessor {

    private final long slowExecutionThresholdMs;
    private final Map<String, String> layerRegistry;
    private final MeterRegistry meterRegistry;

    public ServiceLayerBeanPostProcessor(@Qualifier("LayerRegistry") Map<String, String> layerRegistry,
                                         @Value("${slow-execution-treshold}") long slowExecutionThresholdMs,
                                         MeterRegistry meterRegistry) {
        this.layerRegistry = layerRegistry;
        this.slowExecutionThresholdMs = slowExecutionThresholdMs;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);

        if (!targetClass.isAnnotationPresent(ServiceLayer.class)) {
            return bean;
        }

        ProxyFactory factory = new ProxyFactory(bean);
        factory.addAdvice(new LoggingMethodInterceptor(layerRegistry.get
                (ServiceLayer.class.getSimpleName()),
                slowExecutionThresholdMs));
        factory.addAdvice(new MetricsMethodInterceptor(meterRegistry));
        return factory.getProxy();
    }
}
