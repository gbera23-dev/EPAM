package app.strategies.MicroserviceInteraction;

import app.clients.TrainerHistoryServiceClient;
import app.dto.api.request.TrainerWorkloadRequest;
import app.dto.api.request.TrainingRequest;
import app.entities.ActionType;
import app.entities.Trainer;
import app.entities.User;
import app.exceptions.AccessTimeoutException;
import app.services.TrainerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AddHoursToTrainerStrategy implements MicroserviceInteractionStrategy {

    private final TrainerService trainerService;
    private final TrainerHistoryServiceClient trainerHistoryServiceClient;

    @Override
    public Object sendTheRequest(ProceedingJoinPoint pjp) throws Throwable {
        TrainingRequest trainingRequest = (TrainingRequest) pjp.getArgs()[0];
        HttpServletRequest httpServletRequest = (HttpServletRequest) pjp.getArgs()[1];

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
        attemptSendingRequest(httpServletRequest, trainerWorkloadRequest);
        return pjp.proceed();
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
