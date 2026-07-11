import app.aspects.microserviceInteraction.TrainerHistoryServiceAspect;
import app.clients.TrainerHistoryServiceClient;
import app.dto.api.request.TrainingRequest;
import app.entities.Trainer;
import app.entities.Training;
import app.entities.User;
import app.exceptions.AccessTimeoutException;
import app.services.TraineeService;
import app.services.TrainerService;
import app.services.TrainingService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerHistoryServiceAspectTest {

    @Mock
    private TrainerHistoryServiceClient trainerHistoryServiceClient;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private TraineeService traineeService;

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private JoinPoint jp;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private TrainerHistoryServiceAspect aspect;

    private static final String AUTH_HEADER = "Bearer test-token";
    private static final String TRAINER_USERNAME = "john.doe";
    private static final LocalDate TRAINING_DATE = LocalDate.of(2025, 1, 15);
    private static final int TRAINING_DURATION = 90;

    private User buildUser() {
        return new User(1L, "John", "Doe", TRAINER_USERNAME, "pass", true, null, null);
    }

    private Trainer buildTrainer(User user) {
        return new Trainer(1L, null, user, null, null);
    }

    private Training buildTraining(Trainer trainer) {
        return new Training(1L, null, trainer, "Yoga", null, TRAINING_DATE, TRAINING_DURATION);
    }

    @Test
    void testAddHoursToTrainerWorkloadSendsAddRequest() {
        TrainingRequest trainingRequest = mock(TrainingRequest.class);
        when(trainingRequest.getTrainerUsername()).thenReturn(TRAINER_USERNAME);
        when(trainingRequest.getDate()).thenReturn(TRAINING_DATE);
        when(trainingRequest.getDuration()).thenReturn(TRAINING_DURATION);

        User user = buildUser();
        Trainer trainer = buildTrainer(user);

        when(jp.getArgs()).thenReturn(new Object[]{trainingRequest, httpServletRequest});
        when(trainerService.selectTrainerProfileByUsername(TRAINER_USERNAME)).thenReturn(trainer);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_HEADER);
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(), eq(AUTH_HEADER)))
                .thenReturn(ResponseEntity.ok("ok"));

        aspect.addHoursToTrainerWorkload(jp);

        verify(trainerHistoryServiceClient).updateTrainerWorkload(any(), eq(AUTH_HEADER));
    }

    @Test
    void testAddHoursToTrainerWorkloadGatewayTimeoutThrowsAccessTimeoutException() {
        TrainingRequest trainingRequest = mock(TrainingRequest.class);
        when(trainingRequest.getTrainerUsername()).thenReturn(TRAINER_USERNAME);
        when(trainingRequest.getDate()).thenReturn(TRAINING_DATE);
        when(trainingRequest.getDuration()).thenReturn(TRAINING_DURATION);

        User user = buildUser();
        Trainer trainer = buildTrainer(user);

        when(jp.getArgs()).thenReturn(new Object[]{trainingRequest, httpServletRequest});
        when(trainerService.selectTrainerProfileByUsername(TRAINER_USERNAME)).thenReturn(trainer);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_HEADER);
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(), eq(AUTH_HEADER)))
                .thenReturn(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("timeout"));

        assertThrows(AccessTimeoutException.class, () -> aspect.addHoursToTrainerWorkload(jp));
    }

    @Test
    void testRemoveHoursFromTrainerWorkloadProceedReturnsResult() throws Throwable {
        User user = buildUser();
        Trainer trainer = buildTrainer(user);
        Training training = buildTraining(trainer);

        when(pjp.getArgs()).thenReturn(new Object[]{1L, httpServletRequest});
        when(trainingService.selectTraining(1L)).thenReturn(training);
        when(pjp.proceed()).thenReturn("deleted");
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_HEADER);
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(), eq(AUTH_HEADER)))
                .thenReturn(ResponseEntity.ok("ok"));

        Object result = aspect.removeHoursFromTrainerWorkload(pjp);

        assertEquals("deleted", result);
        verify(trainerHistoryServiceClient).updateTrainerWorkload(any(), eq(AUTH_HEADER));
    }

    @Test
    void testRemoveHoursFromTrainerWorkloadGatewayTimeoutThrowsAccessTimeoutException() throws Throwable {
        User user = buildUser();
        Trainer trainer = buildTrainer(user);
        Training training = buildTraining(trainer);

        when(pjp.getArgs()).thenReturn(new Object[]{1L, httpServletRequest});
        when(trainingService.selectTraining(1L)).thenReturn(training);
        when(pjp.proceed()).thenReturn("deleted");
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_HEADER);
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(), eq(AUTH_HEADER)))
                .thenReturn(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body("timeout"));

        assertThrows(AccessTimeoutException.class, () -> aspect.removeHoursFromTrainerWorkload(pjp));
    }

    @Test
    void testRemoveHoursFromTrainerWorkloadProceedThrowsClientNotCalled() throws Throwable {
        User user = buildUser();
        Trainer trainer = buildTrainer(user);
        Training training = buildTraining(trainer);

        when(pjp.getArgs()).thenReturn(new Object[]{1L, httpServletRequest});
        when(trainingService.selectTraining(1L)).thenReturn(training);
        when(pjp.proceed()).thenThrow(new RuntimeException("db error"));

        assertThrows(RuntimeException.class, () -> aspect.removeHoursFromTrainerWorkload(pjp));
        verifyNoInteractions(trainerHistoryServiceClient);
    }

    @Test
    void testUpdateTrainerHoursAfterTrainerDeletionProceedReturnsResult() throws Throwable {
        User user = buildUser();
        Trainer trainer = buildTrainer(user);
        Training training = buildTraining(trainer);

        when(pjp.getArgs()).thenReturn(new Object[]{TRAINER_USERNAME, httpServletRequest});
        when(traineeService.getAllTrainingsForTrainee(TRAINER_USERNAME)).thenReturn(List.of(training));
        when(pjp.proceed()).thenReturn("deleted");
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_HEADER);

        Object result = aspect.updateTrainerHoursAfterTrainerDeletion(pjp);

        assertEquals("deleted", result);
        verify(trainerHistoryServiceClient).updateTrainersWorkloadInBatch(anyList(), eq(AUTH_HEADER));
    }

    @Test
    void testUpdateTrainerHoursAfterTrainerDeletionEmptyListCallsBatchWithEmptyList() throws Throwable {
        when(pjp.getArgs()).thenReturn(new Object[]{TRAINER_USERNAME, httpServletRequest});
        when(traineeService.getAllTrainingsForTrainee(TRAINER_USERNAME)).thenReturn(List.of());
        when(pjp.proceed()).thenReturn("deleted");
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_HEADER);

        aspect.updateTrainerHoursAfterTrainerDeletion(pjp);

        verify(trainerHistoryServiceClient).updateTrainersWorkloadInBatch(eq(List.of()), eq(AUTH_HEADER));
    }

    @Test
    void testUpdateTrainerHoursAfterTrainerDeletionProceedThrowsClientNotCalled() throws Throwable {
        User user = buildUser();
        Trainer trainer = buildTrainer(user);
        Training training = buildTraining(trainer);

        when(pjp.getArgs()).thenReturn(new Object[]{TRAINER_USERNAME, httpServletRequest});
        when(traineeService.getAllTrainingsForTrainee(TRAINER_USERNAME)).thenReturn(List.of(training));
        when(pjp.proceed()).thenThrow(new RuntimeException("deletion failed"));

        assertThrows(RuntimeException.class, () -> aspect.updateTrainerHoursAfterTrainerDeletion(pjp));
        verifyNoInteractions(trainerHistoryServiceClient);
    }
}