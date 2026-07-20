package app.strategies.MicroserviceInteraction;

import app.clients.TrainerHistoryServiceMessaging;
import app.dto.api.request.TrainerWorkloadRequest;
import app.dto.api.request.TrainingRequest;
import app.entities.ActionType;
import app.entities.Trainer;
import app.entities.User;
import app.services.TrainerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class AddHoursToTrainerStrategy implements MicroserviceInteractionStrategy {

    private final TrainerService trainerService;
    private final TrainerHistoryServiceMessaging trainerHistoryServiceMessaging;

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

    private void attemptSendingRequest(HttpServletRequest httpServletRequest, TrainerWorkloadRequest trainerWorkloadRequest)
            throws JsonProcessingException {
        trainerHistoryServiceMessaging.sendMessage("training-update-channel",
                trainerWorkloadRequest, httpServletRequest.getHeader(AUTHORIZATION_HEADER),
                (String)MDC.get("transactionId"));
    }

}
