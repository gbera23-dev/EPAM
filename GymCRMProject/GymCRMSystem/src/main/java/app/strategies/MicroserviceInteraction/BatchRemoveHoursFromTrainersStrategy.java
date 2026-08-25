package app.strategies.MicroserviceInteraction;

import app.clients.TrainerHistoryServiceMessaging;
import app.dto.api.request.TrainerWorkloadBatchRequest;
import app.dto.api.request.TrainerWorkloadRequest;
import app.entities.ActionType;
import app.entities.Training;
import app.services.TraineeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;
import app.utils.TransactionConstants.*;

import java.util.List;

import static app.utils.MessagingConstants.TRAINING_BATCH_UPDATE_CHANNEL;
import static app.utils.SecurityConstants.AUTHORIZATION_HEADER;
import static app.utils.TransactionConstants.MDC_KEY;

@Component
@AllArgsConstructor
@Slf4j
public class BatchRemoveHoursFromTrainersStrategy implements MicroserviceInteractionStrategy {

    private final TraineeService traineeService;
    private final TrainerHistoryServiceMessaging trainerHistoryServiceMessaging;

    @Override
    public Object sendTheRequest(MethodInvocation invocation) throws Throwable {
        String username = (String) invocation.getArguments()[0];
        HttpServletRequest httpServletRequest = (HttpServletRequest) invocation.getArguments()[1];

        List<Training> trainings = traineeService.getAllTrainingsForTrainee(username);

        Object obj = invocation.proceed();

        trainerHistoryServiceMessaging.sendMessage(
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
