package app.messaging.strategies.impl;

import app.api.dto.request.TrainerWorkloadBatchRequest;
import app.api.dto.request.TrainerWorkloadRequest;
import app.domain.entities.ActionType;
import app.domain.entities.Training;
import app.messaging.microserviceCommunication.TrainerHistoryServiceCommunication;
import app.messaging.strategies.interfaces.MicroserviceInteractionStrategy;
import app.services.business.interfaces.TraineeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;

import java.util.List;

import static app.infrastructure.constants.MessagingConstants.TRAINING_BATCH_UPDATE_CHANNEL;
import static app.infrastructure.constants.SecurityConstants.AUTHORIZATION_HEADER;
import static app.infrastructure.constants.TransactionConstants.MDC_KEY;

@Component
@AllArgsConstructor
@Slf4j
public class BatchRemoveHoursFromTrainersStrategy implements MicroserviceInteractionStrategy {

    private final TraineeService traineeService;
    private final TrainerHistoryServiceCommunication trainerHistoryServiceCommunication;

    @Override
    public Object sendTheRequest(MethodInvocation invocation) throws Throwable {
        String username = (String) invocation.getArguments()[0];
        HttpServletRequest httpServletRequest = (HttpServletRequest) invocation.getArguments()[1];

        List<Training> trainings = traineeService.getAllTrainingsForTrainee(username);

        Object obj = invocation.proceed();

        trainerHistoryServiceCommunication.sendMessage(
                TRAINING_BATCH_UPDATE_CHANNEL,
                new TrainerWorkloadBatchRequest(trainings.stream().map(
                        tr -> new
                                TrainerWorkloadRequest(tr.getTrainer().getUser().getUsername(),
                                tr.getTrainer().getUser().getFirstName(),
                                tr.getTrainer().getUser().getLastName(),
                                tr.getTrainer().getUser().isActive(),
                                tr.getDate(),
                                tr.getDuration(),
                                ActionType.DELETE
                        )
                ).toList()),
                httpServletRequest.getHeader(AUTHORIZATION_HEADER),
                (String) MDC.get(MDC_KEY)
        );
        return obj;
    }
}
