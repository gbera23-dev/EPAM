package app.strategies.MicroserviceInteraction;

import app.clients.TrainerHistoryServiceMessaging;
import app.dto.api.request.TrainerWorkloadRequest;
import app.dto.api.request.TrainingRequest;
import app.entities.ActionType;
import app.entities.Trainer;
import app.entities.User;
import app.services.business.interfaces.TrainerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;

import static app.utils.SecurityConstants.AUTHORIZATION_HEADER;
import static app.utils.MessagingConstants.TRAINING_UPDATE_CHANNEL;
import static app.utils.TransactionConstants.MDC_KEY;

@Component
@AllArgsConstructor
@Slf4j
public class AddHoursToTrainerStrategy implements MicroserviceInteractionStrategy {

    private final TrainerService trainerService;
    private final TrainerHistoryServiceMessaging trainerHistoryServiceMessaging;

    @Override
    public Object sendTheRequest(MethodInvocation invocation) throws Throwable {
        TrainingRequest trainingRequest = (TrainingRequest) invocation.getArguments()[0];
        HttpServletRequest httpServletRequest = (HttpServletRequest) invocation.getArguments()[1];

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
        Object result = invocation.proceed();
        attemptSendingRequest(httpServletRequest, trainerWorkloadRequest);
        return result;
    }

    private void attemptSendingRequest(HttpServletRequest httpServletRequest, TrainerWorkloadRequest trainerWorkloadRequest)
            throws JsonProcessingException {
        trainerHistoryServiceMessaging.sendMessage(TRAINING_UPDATE_CHANNEL,
                trainerWorkloadRequest, httpServletRequest.getHeader(AUTHORIZATION_HEADER),
                (String)MDC.get(MDC_KEY));
    }

}
