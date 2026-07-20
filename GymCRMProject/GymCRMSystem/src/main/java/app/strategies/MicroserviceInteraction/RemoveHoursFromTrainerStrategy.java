package app.strategies.MicroserviceInteraction;

import app.clients.TrainerHistoryServiceClient;
import app.clients.TrainerHistoryServiceMessaging;
import app.dto.api.request.TrainerWorkloadRequest;
import app.entities.ActionType;
import app.entities.Trainer;
import app.entities.Training;
import app.entities.User;
import app.exceptions.AccessTimeoutException;
import app.services.TrainingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final TrainerHistoryServiceMessaging trainerHistoryServiceMessaging;

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

    private void attemptSendingRequest(HttpServletRequest httpServletRequest, TrainerWorkloadRequest trainerWorkloadRequest) throws JsonProcessingException {

        trainerHistoryServiceMessaging.sendMessage(
                "training-update-channel",
                trainerWorkloadRequest,
                httpServletRequest.getHeader(AUTHORIZATION_HEADER)
                );
    }
}
