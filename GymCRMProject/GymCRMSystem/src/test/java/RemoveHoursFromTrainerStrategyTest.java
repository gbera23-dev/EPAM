import app.clients.TrainerHistoryServiceClient;
import app.dto.api.request.TrainerWorkloadRequest;
import app.entities.ActionType;
import app.entities.Trainer;
import app.entities.Training;
import app.entities.User;
import app.exceptions.AccessTimeoutException;
import app.services.TrainingService;
import app.strategies.MicroserviceInteraction.RemoveHoursFromTrainerStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveHoursFromTrainerStrategyTest {

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainerHistoryServiceClient trainerHistoryServiceClient;

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private RemoveHoursFromTrainerStrategy strategy;

    private static final String AUTH_TOKEN = "Bearer test-token";

    private Training buildTraining() {
        User user = new User();
        user.setUsername("john.doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);

        Training training = new Training();
        training.setTrainer(trainer);
        training.setDate(LocalDate.of(2024, 6, 15));
        training.setDuration(60);
        return training;
    }

    @Test
    void testSendTheRequestOnValidRequest() throws Throwable {
        Training training = buildTraining();

        when(pjp.getArgs()).thenReturn(new Object[]{1L, httpServletRequest});
        when(trainingService.selectTraining(1L)).thenReturn(training);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_TOKEN);
        when(pjp.proceed()).thenReturn("proceeded");
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(TrainerWorkloadRequest.class), eq(AUTH_TOKEN)))
                .thenReturn(ResponseEntity.ok("success"));

        Object result = strategy.sendTheRequest(pjp);

        assertEquals("proceeded", result);
        verify(trainerHistoryServiceClient, times(1))
                .updateTrainerWorkload(any(TrainerWorkloadRequest.class), eq(AUTH_TOKEN));
    }

    @Test
    void testSendTheRequestOnCorrectWorkloadRequestBuilt() throws Throwable {
        Training training = buildTraining();

        when(pjp.getArgs()).thenReturn(new Object[]{1L, httpServletRequest});
        when(trainingService.selectTraining(1L)).thenReturn(training);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_TOKEN);
        when(pjp.proceed()).thenReturn(null);
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(TrainerWorkloadRequest.class), eq(AUTH_TOKEN)))
                .thenReturn(ResponseEntity.ok("success"));

        strategy.sendTheRequest(pjp);

        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(trainerHistoryServiceClient).updateTrainerWorkload(captor.capture(), eq(AUTH_TOKEN));

        TrainerWorkloadRequest captured = captor.getValue();
        assertEquals("john.doe", captured.getUsername());
        assertEquals("John", captured.getFirstName());
        assertEquals("Doe", captured.getLastName());
        assertTrue(captured.getIsActive());
        assertEquals(LocalDate.of(2024, 6, 15), captured.getTrainingDate());
        assertEquals(60, captured.getDuration());
        assertEquals(ActionType.DELETE, captured.getActionType());
    }

    @Test
    void testSendTheRequestOnProceedCalledBeforeClientCall() throws Throwable {
        Training training = buildTraining();

        when(pjp.getArgs()).thenReturn(new Object[]{1L, httpServletRequest});
        when(trainingService.selectTraining(1L)).thenReturn(training);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_TOKEN);
        when(pjp.proceed()).thenReturn(null);
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(), any()))
                .thenReturn(ResponseEntity.ok("success"));

        strategy.sendTheRequest(pjp);

        inOrder(pjp, trainerHistoryServiceClient).verify(pjp).proceed();
        inOrder(pjp, trainerHistoryServiceClient).verify(trainerHistoryServiceClient)
                .updateTrainerWorkload(any(), any());
    }

    @Test
    void testSendTheRequestOnGatewayTimeout() throws Throwable {
        Training training = buildTraining();

        when(pjp.getArgs()).thenReturn(new Object[]{1L, httpServletRequest});
        when(trainingService.selectTraining(1L)).thenReturn(training);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_TOKEN);
        when(pjp.proceed()).thenReturn(null);
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(TrainerWorkloadRequest.class), eq(AUTH_TOKEN)))
                .thenReturn(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build());

        assertThrows(AccessTimeoutException.class, () -> strategy.sendTheRequest(pjp));
    }

    @Test
    void testSendTheRequestOnProceedThrowsException() throws Throwable {
        Training training = buildTraining();

        when(pjp.getArgs()).thenReturn(new Object[]{1L, httpServletRequest});
        when(trainingService.selectTraining(1L)).thenReturn(training);
        when(pjp.proceed()).thenThrow(new RuntimeException("proceed failed"));

        assertThrows(RuntimeException.class, () -> strategy.sendTheRequest(pjp));
        verifyNoInteractions(trainerHistoryServiceClient);
    }

    @Test
    void testSendTheRequestOnTrainingNotFound() {
        when(pjp.getArgs()).thenReturn(new Object[]{99L, httpServletRequest});
        when(trainingService.selectTraining(99L)).thenThrow(new RuntimeException("Training not found"));

        assertThrows(RuntimeException.class, () -> strategy.sendTheRequest(pjp));
        verifyNoInteractions(trainerHistoryServiceClient);
    }
}
