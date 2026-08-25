package app.methodInterceptors;

import app.annotations.InteractsWithTraineeHistoryService;
import app.strategies.MicroserviceInteraction.MicroserviceInteractionStrategy;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;

@RequiredArgsConstructor
public class TrainerHistoryMethodInterceptor implements MethodInterceptor {

    private final ApplicationContext applicationContext;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        InteractsWithTraineeHistoryService annotation =
                invocation.getMethod().getAnnotation(InteractsWithTraineeHistoryService.class);

        if (annotation == null) {
            return invocation.proceed();
        }

        Class<?> strategyClass = annotation.chosenStrategy();
        MicroserviceInteractionStrategy strategy =
                (MicroserviceInteractionStrategy) applicationContext.getBean(strategyClass);

        return strategy.sendTheRequest(invocation);
    }
}