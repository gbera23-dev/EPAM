package com.example.Trainer_history_service.beanPostProcessor;

import com.example.Trainer_history_service.methodInterceptors.JWTMethodInterceptor;
import com.example.Trainer_history_service.methodInterceptors.TransactionMethodInterceptor;
import com.example.Trainer_history_service.services.JWTService;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConsumerLayerBeanPostProcessor implements BeanPostProcessor {

    private final Map<String, String> layerRegistry;
    private final JWTService jwtService;

    public ConsumerLayerBeanPostProcessor(@Qualifier("LayerRegistry") Map<String, String> layerRegistry,
                                             JWTService jwtService) {
        this.layerRegistry = layerRegistry;
        this.jwtService = jwtService;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        String packageName = bean.getClass().getPackageName();

        if (!packageName.equals("com.example.Trainer_history_service.consumers")) {
            return bean;
        }

        ProxyFactory factory = new ProxyFactory(bean);
        factory.addAdvice(new JWTMethodInterceptor(jwtService));
        factory.addAdvice(new TransactionMethodInterceptor());
        return factory.getProxy();
    }

}
