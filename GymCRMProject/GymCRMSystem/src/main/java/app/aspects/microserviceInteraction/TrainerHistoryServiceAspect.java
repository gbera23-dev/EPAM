package app.aspects.microserviceInteraction;

import app.annotations.InteractsWithTraineeHistoryService;
import app.strategies.MicroserviceInteraction.MicroserviceInteractionStrategy;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Aspect
@AllArgsConstructor
@Component
public class TrainerHistoryServiceAspect {

    private final ApplicationContext applicationContext;

    @Around(value = "@annotation(interactsWithTraineeHistoryService)")
    public Object sendRequestToMicroservice(ProceedingJoinPoint pjp,
                                            InteractsWithTraineeHistoryService interactsWithTraineeHistoryService)
            throws Throwable {
        Class cls = interactsWithTraineeHistoryService.chosenStrategy();
        MicroserviceInteractionStrategy microserviceInteractionStrategy = (MicroserviceInteractionStrategy)
                applicationContext.getBean(cls);
        return microserviceInteractionStrategy.sendTheRequest(pjp);
    }

}
