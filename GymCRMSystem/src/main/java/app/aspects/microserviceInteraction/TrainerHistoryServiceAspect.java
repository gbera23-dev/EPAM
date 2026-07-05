package app.aspects.microserviceInteraction;

import app.clients.TrainerHistoryServiceClient;
import app.dto.api.request.TrainerWorkloadRequest;
import app.dto.api.request.TrainingRequest;
import app.entities.ActionType;
import app.entities.Trainer;
import app.entities.User;
import app.services.TrainerService;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@AllArgsConstructor
@Component
public class TrainerHistoryServiceAspect {

    private final TrainerHistoryServiceClient trainerHistoryServiceClient;
    private final TrainerService trainerService;

    @Pointcut("execution(* app.restcontroller.TrainingRestController.*(..))")
    public void restControllerLayer() {}

    @After("restControllerLayer()")
    public void updateTrainerWorkload(JoinPoint jp) {
        TrainingRequest trainingRequest = (TrainingRequest) jp.getArgs()[0];

        Trainer trainer = trainerService.
                selectTrainerProfileByUsername(trainingRequest.getTrainerUsername());
        User user = trainer.getUser();

        TrainerWorkloadRequest trainerWorkloadRequest = new TrainerWorkloadRequest(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.isActive(),
                trainingRequest.getDate(),
                trainingRequest.getDuration(),
                ActionType.ADD
        );

        ResponseEntity<String> resp =
                trainerHistoryServiceClient.updateTrainerWorkload(trainerWorkloadRequest);

    }

}
