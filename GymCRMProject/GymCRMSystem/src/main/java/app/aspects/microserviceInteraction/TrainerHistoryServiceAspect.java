package app.aspects.microserviceInteraction;

import app.annotations.InteractsWithTraineeHistoryService;
import app.clients.TrainerHistoryServiceClient;
import app.services.TraineeService;
import app.services.TrainerService;
import app.services.TrainingService;
import app.strategies.MicroserviceInteraction.MicroserviceInteractionStrategy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Aspect
@AllArgsConstructor
@Slf4j
@Component
public class TrainerHistoryServiceAspect {

    private final ApplicationContext applicationContext;

    @Around(value = "@annotation(interactsWithTraineeHistoryService)")
    public Object sendRequestToMicroservice(ProceedingJoinPoint pjp,
                                            InteractsWithTraineeHistoryService interactsWithTraineeHistoryService)
            throws Throwable {
        Class cls = interactsWithTraineeHistoryService.chosenStrategy();
        log.info("chosen class is {}", cls.getName());
        MicroserviceInteractionStrategy microserviceInteractionStrategy = (MicroserviceInteractionStrategy)
                applicationContext.getBean(cls);
        return microserviceInteractionStrategy.sendTheRequest(pjp);
    }

}
