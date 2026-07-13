import app.clients.TrainerHistoryServiceClient;
import app.dto.api.request.TrainerWorkloadRequest;
import app.entities.ActionType;
import app.entities.Trainer;
import app.entities.Training;
import app.entities.User;
import app.services.TraineeService;
import app.strategies.MicroserviceInteraction.BatchRemoveHoursFromTrainersStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchRemoveHoursFromTrainersStrategyTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerHistoryServiceClient trainerHistoryServiceClient;

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private BatchRemoveHoursFromTrainersStrategy strategy;

    private static final String AUTH_TOKEN = "Bearer test-token";

    private Training buildTraining(String username, LocalDate date, int duration) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        Training training = new Training();
        training.setTrainer(trainer);
        training.setDate(date);
        training.setDuration(duration);
        return training;
    }

    @Test
    void testSendTheRequestOnValidTrainings() throws Throwable {
        List<Training> trainings = List.of(
                buildTraining("john.doe", LocalDate.of(2024, 6, 15), 60),
                buildTraining("jane.doe", LocalDate.of(2024, 7, 10), 90)
        );

        when(pjp.getArgs()).thenReturn(new Object[]{"trainee.user", httpServletRequest});
        when(traineeService.getAllTrainingsForTrainee("trainee.user")).thenReturn(trainings);
        when(pjp.proceed()).thenReturn("proceeded");
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_TOKEN);

        Object result = strategy.sendTheRequest(pjp);

        assertEquals("proceeded", result);
        verify(trainerHistoryServiceClient, times(1))
                .updateTrainersWorkloadInBatch(anyList(), eq(AUTH_TOKEN));
    }

    @Test
    void testSendTheRequestOnCorrectWorkloadRequestsBuilt() throws Throwable {
        Training training = buildTraining("john.doe", LocalDate.of(2024, 6, 15), 60);

        when(pjp.getArgs()).thenReturn(new Object[]{"trainee.user", httpServletRequest});
        when(traineeService.getAllTrainingsForTrainee("trainee.user")).thenReturn(List.of(training));
        when(pjp.proceed()).thenReturn(null);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_TOKEN);

        strategy.sendTheRequest(pjp);

        ArgumentCaptor<List<TrainerWorkloadRequest>> captor = ArgumentCaptor.forClass(List.class);
        verify(trainerHistoryServiceClient).updateTrainersWorkloadInBatch(captor.capture(), eq(AUTH_TOKEN));

        TrainerWorkloadRequest captured = captor.getValue().get(0);
        assertEquals("john.doe", captured.getUsername());
        assertEquals("John", captured.getFirstName());
        assertEquals("Doe", captured.getLastName());
        assertTrue(captured.getIsActive());
        assertEquals(LocalDate.of(2024, 6, 15), captured.getTrainingDate());
        assertEquals(60, captured.getDuration());
        assertEquals(ActionType.DELETE, captured.getActionType());
    }

    @Test
    void testSendTheRequestOnEmptyTrainingList() throws Throwable {
        when(pjp.getArgs()).thenReturn(new Object[]{"trainee.user", httpServletRequest});
        when(traineeService.getAllTrainingsForTrainee("trainee.user")).thenReturn(Collections.emptyList());
        when(pjp.proceed()).thenReturn(null);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_TOKEN);

        strategy.sendTheRequest(pjp);

        ArgumentCaptor<List<TrainerWorkloadRequest>> captor = ArgumentCaptor.forClass(List.class);
        verify(trainerHistoryServiceClient).updateTrainersWorkloadInBatch(captor.capture(), eq(AUTH_TOKEN));
        assertTrue(captor.getValue().isEmpty());
    }

    @Test
    void testSendTheRequestOnProceedCalledBeforeClientCall() throws Throwable {
        Training training = buildTraining("john.doe", LocalDate.of(2024, 6, 15), 60);

        when(pjp.getArgs()).thenReturn(new Object[]{"trainee.user", httpServletRequest});
        when(traineeService.getAllTrainingsForTrainee("trainee.user")).thenReturn(List.of(training));
        when(pjp.proceed()).thenReturn(null);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_TOKEN);

        strategy.sendTheRequest(pjp);

        inOrder(pjp, trainerHistoryServiceClient).verify(pjp).proceed();
        inOrder(pjp, trainerHistoryServiceClient).verify(trainerHistoryServiceClient)
                .updateTrainersWorkloadInBatch(anyList(), any());
    }

    @Test
    void testSendTheRequestOnProceedThrowsException() throws Throwable {
        List<Training> trainings = List.of(
                buildTraining("john.doe", LocalDate.of(2024, 6, 15), 60)
        );

        when(pjp.getArgs()).thenReturn(new Object[]{"trainee.user", httpServletRequest});
        when(traineeService.getAllTrainingsForTrainee("trainee.user")).thenReturn(trainings);
        when(pjp.proceed()).thenThrow(new RuntimeException("proceed failed"));

        assertThrows(RuntimeException.class, () -> strategy.sendTheRequest(pjp));
        verifyNoInteractions(trainerHistoryServiceClient);
    }

    @Test
    void testSendTheRequestOnTraineeNotFound() {
        when(pjp.getArgs()).thenReturn(new Object[]{"unknown.user", httpServletRequest});
        when(traineeService.getAllTrainingsForTrainee("unknown.user"))
                .thenThrow(new RuntimeException("Trainee not found"));

        assertThrows(RuntimeException.class, () -> strategy.sendTheRequest(pjp));
        verifyNoInteractions(trainerHistoryServiceClient);
    }

    @Test
    void testSendTheRequestOnMultipleTrainersCorrectCount() throws Throwable {
        List<Training> trainings = List.of(
                buildTraining("trainer.one", LocalDate.of(2024, 1, 10), 30),
                buildTraining("trainer.two", LocalDate.of(2024, 2, 20), 45),
                buildTraining("trainer.three", LocalDate.of(2024, 3, 5), 90)
        );

        when(pjp.getArgs()).thenReturn(new Object[]{"trainee.user", httpServletRequest});
        when(traineeService.getAllTrainingsForTrainee("trainee.user")).thenReturn(trainings);
        when(pjp.proceed()).thenReturn(null);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_TOKEN);

        strategy.sendTheRequest(pjp);

        ArgumentCaptor<List<TrainerWorkloadRequest>> captor = ArgumentCaptor.forClass(List.class);
        verify(trainerHistoryServiceClient).updateTrainersWorkloadInBatch(captor.capture(), eq(AUTH_TOKEN));
        assertEquals(3, captor.getValue().size());
    }
}
