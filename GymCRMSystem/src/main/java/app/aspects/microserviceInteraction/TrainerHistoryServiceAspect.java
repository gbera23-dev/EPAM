package app.aspects.microserviceInteraction;

import app.clients.TrainerHistoryServiceClient;
import app.dto.api.request.TrainerWorkloadRequest;
import app.dto.api.request.TrainingRequest;
import app.entities.ActionType;
import app.entities.Trainer;
import app.entities.Training;
import app.entities.User;
import app.exceptions.AccessTimeoutException;
import app.services.TrainerService;
import app.services.TrainingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@AllArgsConstructor
@Slf4j
@Component
public class TrainerHistoryServiceAspect {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final TrainerHistoryServiceClient trainerHistoryServiceClient;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Pointcut("execution(* app.restcontroller.TrainingRestController.addTraining(..))")
    public void addTraining() {}

    @Pointcut("execution(* app.restcontroller.TrainingRestController.deleteTraining(..))")
    public void deleteTraining() {}

    @After("addTraining()")
    public void addHoursToTrainerWorkload(JoinPoint jp) {

        TrainingRequest trainingRequest = (TrainingRequest) jp.getArgs()[0];
        HttpServletRequest httpServletRequest = (HttpServletRequest) jp.getArgs()[1];

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
    }

    @Around("deleteTraining()")
        public Object removeHoursFromTrainerWorkload(ProceedingJoinPoint pjp) throws Throwable {
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

        Object obj = null;

        obj = pjp.proceed();

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
