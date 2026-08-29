package app.messaging.strategies.impl;

import app.api.dto.request.TrainerWorkloadRequest;
import app.domain.entities.ActionType;
import app.domain.entities.Trainer;
import app.domain.entities.Training;
import app.domain.entities.User;
import app.messaging.microserviceCommunication.TrainerHistoryServiceCommunication;
import app.messaging.strategies.interfaces.MicroserviceInteractionStrategy;
import app.services.business.interfaces.TrainingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInvocation;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;

import static app.infrastructure.constants.MessagingConstants.TRAINING_UPDATE_CHANNEL;
import static app.infrastructure.constants.SecurityConstants.AUTHORIZATION_HEADER;
import static app.infrastructure.constants.TransactionConstants.MDC_KEY;

@Component
@AllArgsConstructor
public class RemoveHoursFromTrainerStrategy implements MicroserviceInteractionStrategy {

    private final TrainingService trainingService;
    private final TrainerHistoryServiceCommunication trainerHistoryServiceCommunication;

    @Override
    public Object sendTheRequest(MethodInvocation invocation) throws Throwable {
        Long trainingId = (Long) invocation.getArguments()[0];
        HttpServletRequest httpServletRequest = (HttpServletRequest) invocation.getArguments()[1];

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

        Object obj = invocation.proceed();

        attemptSendingRequest(httpServletRequest, trainerWorkloadRequest);

        return obj;
    }

    private void attemptSendingRequest(HttpServletRequest httpServletRequest, TrainerWorkloadRequest trainerWorkloadRequest) throws JsonProcessingException {

        trainerHistoryServiceCommunication.sendMessage(
                TRAINING_UPDATE_CHANNEL,
                trainerWorkloadRequest,
                httpServletRequest.getHeader(AUTHORIZATION_HEADER),
                (String) MDC.get(MDC_KEY)
        );
    }
}
