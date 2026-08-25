package app.messaging.strategies.impl;

import app.api.dto.request.TrainerWorkloadRequest;
import app.api.dto.request.TrainingRequest;
import app.domain.entities.ActionType;
import app.domain.entities.Trainer;
import app.domain.entities.User;
import app.messaging.microserviceCommunication.TrainerHistoryServiceCommunication;
import app.messaging.strategies.interfaces.MicroserviceInteractionStrategy;
import app.services.business.interfaces.TrainerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;

import static app.infrastructure.constants.SecurityConstants.AUTHORIZATION_HEADER;
import static app.infrastructure.constants.MessagingConstants.TRAINING_UPDATE_CHANNEL;
import static app.infrastructure.constants.TransactionConstants.MDC_KEY;

@Component
@AllArgsConstructor
@Slf4j
public class AddHoursToTrainerStrategy implements MicroserviceInteractionStrategy {

    private final TrainerService trainerService;
    private final TrainerHistoryServiceCommunication trainerHistoryServiceCommunication;

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
        trainerHistoryServiceCommunication.sendMessage(TRAINING_UPDATE_CHANNEL,
                trainerWorkloadRequest, httpServletRequest.getHeader(AUTHORIZATION_HEADER),
                (String)MDC.get(MDC_KEY));
    }

}
