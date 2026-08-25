package app.beanPostProcessor;

import app.annotations.InteractsWithTraineeHistoryService;
import app.methodInterceptors.TrainerHistoryMethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class TrainerHistoryBeanPostProcessor implements BeanPostProcessor {

    private final ApplicationContext applicationContext;

    public TrainerHistoryBeanPostProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!hasAnnotatedMethod(bean.getClass())) {
            return bean;
        }

        ProxyFactory factory = new ProxyFactory(bean);
        factory.addAdvice(new TrainerHistoryMethodInterceptor(applicationContext));
        return factory.getProxy();
    }

    private boolean hasAnnotatedMethod(Class<?> targetClass) {
        for (Method method : targetClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(InteractsWithTraineeHistoryService.class)) {
                return true;
            }
        }
        return false;
    }
}