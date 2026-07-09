import app.aspects.microserviceInteraction.TrainerHistoryServiceAspect;
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
import org.aspectj.lang.JoinPoint;
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
class TrainerHistoryServiceAspectTest {

    @Mock
    private TrainerHistoryServiceClient trainerHistoryServiceClient;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private ProceedingJoinPoint pjp;

    @InjectMocks
    private TrainerHistoryServiceAspect aspect;

    private User buildUser(String username, String firstName, String lastName, boolean active) {
        User user = new User();
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActive(active);
        return user;
    }

    private Trainer buildTrainer(User user) {
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        return trainer;
    }

    private TrainingRequest buildTrainingRequest(String trainerUsername, LocalDate date, int duration) {
        TrainingRequest req = new TrainingRequest();
        req.setTrainerUsername(trainerUsername);
        req.setDate(date);
        req.setDuration(duration);
        return req;
    }

    @Test
    void testAddHoursToTrainerWorkloadSendsAddRequest() {
        User user = buildUser("john.doe", "John", "Doe", true);
        Trainer trainer = buildTrainer(user);
        LocalDate date = LocalDate.of(2024, 6, 15);
        TrainingRequest trainingRequest = buildTrainingRequest("john.doe", date, 2);
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        JoinPoint jp = mock(JoinPoint.class);

        when(jp.getArgs()).thenReturn(new Object[]{trainingRequest, httpRequest});
        when(trainerService.selectTrainerProfileByUsername("john.doe")).thenReturn(trainer);
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(TrainerWorkloadRequest.class), eq("Bearer token")))
                .thenReturn(ResponseEntity.ok("updated"));

        aspect.addHoursToTrainerWorkload(jp);

        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(trainerHistoryServiceClient).updateTrainerWorkload(captor.capture(), eq("Bearer token"));
        TrainerWorkloadRequest sent = captor.getValue();
        assertEquals("john.doe", sent.getUsername());
        assertEquals("John", sent.getFirstName());
        assertEquals("Doe", sent.getLastName());
        assertTrue(sent.getIsActive());
        assertEquals(date, sent.getTrainingDate());
        assertEquals(2, sent.getDuration());
        assertEquals(ActionType.ADD, sent.getActionType());
    }

    @Test
    void testAddHoursToTrainerWorkloadGatewayTimeoutThrowsAccessTimeoutException() {
        User user = buildUser("jane.doe", "Jane", "Doe", true);
        Trainer trainer = buildTrainer(user);
        LocalDate date = LocalDate.of(2024, 7, 1);
        TrainingRequest trainingRequest = buildTrainingRequest("jane.doe", date, 3);
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);
        JoinPoint jp = mock(JoinPoint.class);

        when(jp.getArgs()).thenReturn(new Object[]{trainingRequest, httpRequest});
        when(trainerService.selectTrainerProfileByUsername("jane.doe")).thenReturn(trainer);
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(TrainerWorkloadRequest.class), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build());

        assertThrows(AccessTimeoutException.class, () -> aspect.addHoursToTrainerWorkload(jp));
    }

    @Test
    void testRemoveHoursFromTrainerWorkloadProceedsAndSendsDeleteRequest() throws Throwable {
        User user = buildUser("john.doe", "John", "Doe", true);
        Trainer trainer = buildTrainer(user);
        LocalDate date = LocalDate.of(2024, 5, 20);
        Training training = new Training();
        training.setTrainer(trainer);
        training.setDate(date);
        training.setDuration(1);
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);

        when(pjp.getArgs()).thenReturn(new Object[]{42L, httpRequest});
        when(trainingService.selectTraining(42L)).thenReturn(training);
        when(pjp.proceed()).thenReturn("deleted");
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(TrainerWorkloadRequest.class), eq("Bearer token")))
                .thenReturn(ResponseEntity.ok("updated"));

        Object result = aspect.removeHoursFromTrainerWorkload(pjp);

        assertEquals("deleted", result);
        verify(pjp).proceed();

        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(trainerHistoryServiceClient).updateTrainerWorkload(captor.capture(), eq("Bearer token"));
        TrainerWorkloadRequest sent = captor.getValue();
        assertEquals("john.doe", sent.getUsername());
        assertEquals(date, sent.getTrainingDate());
        assertEquals(1, sent.getDuration());
        assertEquals(ActionType.DELETE, sent.getActionType());
    }

    @Test
    void testRemoveHoursFromTrainerWorkloadProceedThrowsRethrows() throws Throwable {
        User user = buildUser("john.doe", "John", "Doe", true);
        Trainer trainer = buildTrainer(user);
        Training training = new Training();
        training.setTrainer(trainer);
        training.setDate(LocalDate.of(2024, 5, 20));
        training.setDuration(1);
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);

        when(pjp.getArgs()).thenReturn(new Object[]{99L, httpRequest});
        when(trainingService.selectTraining(99L)).thenReturn(training);
        when(pjp.proceed()).thenThrow(new RuntimeException("db error"));

        assertThrows(RuntimeException.class, () -> aspect.removeHoursFromTrainerWorkload(pjp));
        verify(trainerHistoryServiceClient, never()).updateTrainerWorkload(any(), any());
    }

    @Test
    void testRemoveHoursFromTrainerWorkloadGatewayTimeoutThrowsAccessTimeoutException() throws Throwable {
        User user = buildUser("john.doe", "John", "Doe", false);
        Trainer trainer = buildTrainer(user);
        LocalDate date = LocalDate.of(2024, 4, 10);
        Training training = new Training();
        training.setTrainer(trainer);
        training.setDate(date);
        training.setDuration(4);
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);

        when(pjp.getArgs()).thenReturn(new Object[]{7L, httpRequest});
        when(trainingService.selectTraining(7L)).thenReturn(training);
        when(pjp.proceed()).thenReturn("ok");
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(TrainerWorkloadRequest.class), any()))
                .thenReturn(ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build());

        assertThrows(AccessTimeoutException.class, () -> aspect.removeHoursFromTrainerWorkload(pjp));
    }

    @Test
    void testRemoveHoursFromTrainerWorkloadProceedsBeforeSendingRequest() throws Throwable {
        User user = buildUser("john.doe", "John", "Doe", true);
        Trainer trainer = buildTrainer(user);
        Training training = new Training();
        training.setTrainer(trainer);
        training.setDate(LocalDate.of(2024, 3, 5));
        training.setDuration(2);
        HttpServletRequest httpRequest = mock(HttpServletRequest.class);

        when(pjp.getArgs()).thenReturn(new Object[]{5L, httpRequest});
        when(trainingService.selectTraining(5L)).thenReturn(training);
        when(httpRequest.getHeader("Authorization")).thenReturn("Bearer token");
        when(trainerHistoryServiceClient.updateTrainerWorkload(any(), any()))
                .thenReturn(ResponseEntity.ok("ok"));

        var proceedOrder = inOrder(pjp, trainerHistoryServiceClient);
        when(pjp.proceed()).thenReturn("done");

        aspect.removeHoursFromTrainerWorkload(pjp);

        proceedOrder.verify(pjp).proceed();
        proceedOrder.verify(trainerHistoryServiceClient).updateTrainerWorkload(any(), any());
    }
}