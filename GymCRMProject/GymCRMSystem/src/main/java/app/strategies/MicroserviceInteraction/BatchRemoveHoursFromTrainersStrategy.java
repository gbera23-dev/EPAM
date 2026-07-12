package app.strategies.MicroserviceInteraction;

import app.clients.TrainerHistoryServiceClient;
import app.dto.api.request.TrainerWorkloadRequest;
import app.entities.ActionType;
import app.entities.Training;
import app.services.TraineeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@AllArgsConstructor
public class BatchRemoveHoursFromTrainersStrategy implements MicroserviceInteractionStrategy {

    private final TraineeService traineeService;
    private final TrainerHistoryServiceClient trainerHistoryServiceClient;

    @Override
    public Object sendTheRequest(ProceedingJoinPoint pjp) throws Throwable {
        String username = (String) pjp.getArgs()[0];
        HttpServletRequest httpServletRequest = (HttpServletRequest) pjp.getArgs()[1];

        List<Training> trainings = traineeService.getAllTrainingsForTrainee(username);

        Object obj = pjp.proceed();

        trainerHistoryServiceClient.updateTrainersWorkloadInBatch(
                trainings.stream().map(
                        tr -> new
                                TrainerWorkloadRequest(tr.getTrainer().getUser().getUsername(),
                                tr.getTrainer().getUser().getFirstName(),
                                tr.getTrainer().getUser().getLastName(),
                                tr.getTrainer().getUser().isActive(),
                                tr.getDate(),
                                tr.getDuration(),
                                ActionType.DELETE
                        )
                ).toList(), httpServletRequest.getHeader(AUTHORIZATION_HEADER)
        );
        return obj;
    }
}
