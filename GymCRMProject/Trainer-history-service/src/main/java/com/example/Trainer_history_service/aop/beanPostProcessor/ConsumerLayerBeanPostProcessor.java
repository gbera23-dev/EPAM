package com.example.Trainer_history_service.aop.beanPostProcessor;

import com.example.Trainer_history_service.aop.annotations.ConsumerLayer;
import com.example.Trainer_history_service.aop.methodInterceptors.JWTMethodInterceptor;
import com.example.Trainer_history_service.aop.methodInterceptors.TransactionMethodInterceptor;
import com.example.Trainer_history_service.services.security.interfaces.JWTService;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ConsumerLayerBeanPostProcessor implements BeanPostProcessor {

    private final JWTService jwtService;

    public ConsumerLayerBeanPostProcessor(@Lazy JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);

        if (!targetClass.isAnnotationPresent(ConsumerLayer.class)) {
            return bean;
        }

        ProxyFactory factory = new ProxyFactory(bean);
        factory.addAdvice(new JWTMethodInterceptor(jwtService));
        factory.addAdvice(new TransactionMethodInterceptor());
        return factory.getProxy();
    }

}
