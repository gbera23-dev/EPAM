import app.clients.TrainerHistoryServiceClient;
import app.dto.api.request.TrainerWorkloadRequest;
import app.dto.api.request.TrainingRequest;
import app.entities.ActionType;
import app.entities.Trainer;
import app.entities.User;
import app.exceptions.AccessTimeoutException;
import app.services.TrainerService;
import app.strategies.MicroserviceInteraction.AddHoursToTrainerStrategy;
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
class AddHoursToTrainerStrategyTest {

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainerHistoryServiceClient trainerHistoryServiceClient;

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AddHoursToTrainerStrategy strategy;

    private static final String AUTH_TOKEN = "Bearer test-token";

    private TrainingRequest buildTrainingRequest() {
        TrainingRequest request = new TrainingRequest();
        request.setTrainerUsername("john.doe");
        request.setDate(LocalDate.of(2024, 6, 15));
        request.setDuration(60);
        return request;
    }

    private Trainer buildTrainer() {
        User user = new User();
        user.setUsername("john.doe");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setActive(true);

        Trainer trainer = new Trainer();
        trainer.setUser(user);
        return trainer;
    }

    @Test
    void testSendTheRequestOnValidRequest() throws Throwable {
        TrainingRequest trainingRequest = buildTrainingRequest();
        Trainer trainer = buildTrainer();

        when(pjp.getArgs()).thenReturn(new Object[]{trainingRequest, httpServletRequest});
        when(trainerService.selectTrainerProfileByUsername("john.doe")).thenReturn(trainer);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_TOKEN);
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(TrainerWorkloadRequest.class), eq(AUTH_TOKEN)))
                .thenReturn(ResponseEntity.ok("success"));
        when(pjp.proceed()).thenReturn("proceeded");

        Object result = strategy.sendTheRequest(pjp);

        assertEquals("proceeded", result);
        verify(trainerHistoryServiceClient, times(1))
                .updateTrainerWorkload(any(TrainerWorkloadRequest.class), eq(AUTH_TOKEN));
        verify(pjp, times(1)).proceed();
    }

    @Test
    void testSendTheRequestOnCorrectWorkloadRequestBuilt() throws Throwable {
        TrainingRequest trainingRequest = buildTrainingRequest();
        Trainer trainer = buildTrainer();

        when(pjp.getArgs()).thenReturn(new Object[]{trainingRequest, httpServletRequest});
        when(trainerService.selectTrainerProfileByUsername("john.doe")).thenReturn(trainer);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_TOKEN);
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(TrainerWorkloadRequest.class), eq(AUTH_TOKEN)))
                .thenReturn(ResponseEntity.ok("success"));
        when(pjp.proceed()).thenReturn(null);

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
        assertEquals(ActionType.ADD, captured.getActionType());
    }

    @Test
    void testSendTheRequestOnGatewayTimeout() throws Throwable {
        TrainingRequest trainingRequest = buildTrainingRequest();
        Trainer trainer = buildTrainer();

        when(pjp.getArgs()).thenReturn(new Object[]{trainingRequest, httpServletRequest});
        when(trainerService.selectTrainerProfileByUsername("john.doe")).thenReturn(trainer);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_TOKEN);
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(TrainerWorkloadRequest.class), eq(AUTH_TOKEN)))
                .thenReturn(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build());

        assertThrows(AccessTimeoutException.class, () -> strategy.sendTheRequest(pjp));
        verify(pjp, never()).proceed();
    }

    @Test
    void testSendTheRequestOnProceedThrowsException() throws Throwable {
        TrainingRequest trainingRequest = buildTrainingRequest();
        Trainer trainer = buildTrainer();

        when(pjp.getArgs()).thenReturn(new Object[]{trainingRequest, httpServletRequest});
        when(trainerService.selectTrainerProfileByUsername("john.doe")).thenReturn(trainer);
        when(httpServletRequest.getHeader("Authorization")).thenReturn(AUTH_TOKEN);
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(TrainerWorkloadRequest.class), eq(AUTH_TOKEN)))
                .thenReturn(ResponseEntity.ok("success"));
        when(pjp.proceed()).thenThrow(new RuntimeException("proceed failed"));

        assertThrows(RuntimeException.class, () -> strategy.sendTheRequest(pjp));
    }

    @Test
    void testSendTheRequestOnTrainerNotFound() {
        TrainingRequest trainingRequest = buildTrainingRequest();

        when(pjp.getArgs()).thenReturn(new Object[]{trainingRequest, httpServletRequest});
        when(trainerService.selectTrainerProfileByUsername("john.doe"))
                .thenThrow(new RuntimeException("Trainer not found"));

        assertThrows(RuntimeException.class, () -> strategy.sendTheRequest(pjp));
        verifyNoInteractions(trainerHistoryServiceClient);
    }
}
