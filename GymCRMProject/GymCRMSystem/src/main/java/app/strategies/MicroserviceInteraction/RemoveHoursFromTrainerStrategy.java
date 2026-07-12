package app.strategies.MicroserviceInteraction;

import app.clients.TrainerHistoryServiceClient;
import app.dto.api.request.TrainerWorkloadRequest;
import app.entities.ActionType;
import app.entities.Trainer;
import app.entities.Training;
import app.entities.User;
import app.exceptions.AccessTimeoutException;
import app.services.TrainingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RemoveHoursFromTrainerStrategy implements MicroserviceInteractionStrategy {

    private final TrainingService trainingService;
    private final TrainerHistoryServiceClient trainerHistoryServiceClient;

    @Override
    public Object sendTheRequest(ProceedingJoinPoint pjp) throws Throwable {
        Long trainingId = (Long) pjp.getArgs()[0];
        HttpServletRequest httpServletRequest = (HttpServletRequest) pjp.getArgs()[1];

        Training training = trainingService.selectTraining(trainingId);

        Trainer trainer = training.getTrainer();
        User user = trainer.getUser();

        TrainerWorkloadRequest trainerWorkloadRequest = new TrainerWorkloadRequest(
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.isActive(),
                training.getDate(),
                training.getDuration(),
                ActionType.DELETE
        );

        Object obj = pjp.proceed();

        attemptSendingRequest(httpServletRequest, trainerWorkloadRequest);

        return obj;
    }

    private void attemptSendingRequest(HttpServletRequest httpServletRequest, TrainerWorkloadRequest trainerWorkloadRequest) {
        ResponseEntity<String> resp =
                trainerHistoryServiceClient.updateTrainerWorkload(trainerWorkloadRequest,
                        httpServletRequest.getHeader(AUTHORIZATION_HEADER));

        if(resp.getStatusCode().equals(HttpStatus.GATEWAY_TIMEOUT)){
            throw new AccessTimeoutException(
                    "Could not connect to microservice...");
        }
    }
}
