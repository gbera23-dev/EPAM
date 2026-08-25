package com.example.Trainer_history_service.beanPostProcessor;

import com.example.Trainer_history_service.annotations.ConsumerLayer;
import com.example.Trainer_history_service.methodInterceptors.JWTMethodInterceptor;
import com.example.Trainer_history_service.methodInterceptors.TransactionMethodInterceptor;
import com.example.Trainer_history_service.services.JWTService;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

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
