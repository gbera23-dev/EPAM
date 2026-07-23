import app.clients.TrainerHistoryServiceMessaging;
import app.dto.api.request.TrainerWorkloadRequest;
import app.entities.Trainer;
import app.entities.Training;
import app.entities.User;
import app.services.TrainingService;
import app.strategies.MicroserviceInteraction.RemoveHoursFromTrainerStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveHoursFromTrainerStrategyTest {

    @Mock private TrainingService trainingService;
    @Mock private TrainerHistoryServiceMessaging trainerHistoryServiceMessaging;
    @Mock private ProceedingJoinPoint pjp;
    @Mock private HttpServletRequest httpServletRequest;
    @Mock private Training training;
    @Mock private Trainer trainer;
    @Mock private User user;

    private RemoveHoursFromTrainerStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new RemoveHoursFromTrainerStrategy(trainingService, trainerHistoryServiceMessaging);
        MDC.put("transactionId", "txn-2");
    }

    @Test
    void testSendTheRequestProceedsThenSendsMessage() throws Throwable {
        when(pjp.getArgs()).thenReturn(new Object[]{5L, httpServletRequest});
        when(trainingService.selectTraining(5L)).thenReturn(training);
        when(training.getTrainer()).thenReturn(trainer);
        when(trainer.getUser()).thenReturn(user);
        when(training.getDate()).thenReturn(java.time.LocalDate.of(2025, 7, 1));
        when(training.getDuration()).thenReturn(45);
        when(user.getUsername()).thenReturn("trainer.one");
        when(user.getFirstName()).thenReturn("John");
        when(user.getLastName()).thenReturn("Doe");
        when(user.isActive()).thenReturn(true);
        when(httpServletRequest.getHeader(anyString())).thenReturn("Bearer token");
        when(pjp.proceed()).thenReturn("proceeded");

        Object result = strategy.sendTheRequest(pjp);

        var inOrder = inOrder(pjp, trainerHistoryServiceMessaging);
        inOrder.verify(pjp).proceed();
        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        inOrder.verify(trainerHistoryServiceMessaging).sendMessage(
                eq("training-update-channel"), captor.capture(), eq("Bearer token"), eq("txn-2"));

        TrainerWorkloadRequest sent = captor.getValue();
        assertEquals("trainer.one", sent.getUsername());
        assertEquals("John", sent.getFirstName());
        assertEquals("Doe", sent.getLastName());
        assertTrue(sent.getIsActive());
        assertEquals(java.time.LocalDate.of(2025, 7, 1), sent.getTrainingDate());
        assertEquals(45, sent.getDuration());
        assertEquals(app.entities.ActionType.DELETE, sent.getActionType());
        assertEquals("proceeded", result);
    }

    @Test
    void testSendTheRequestUsesTrainingIdFromArgsToLookUpTraining() throws Throwable {
        when(pjp.getArgs()).thenReturn(new Object[]{9L, httpServletRequest});
        when(trainingService.selectTraining(9L)).thenReturn(training);
        when(training.getTrainer()).thenReturn(trainer);
        when(trainer.getUser()).thenReturn(user);
        when(httpServletRequest.getHeader(anyString())).thenReturn("token");
        when(pjp.proceed()).thenReturn("ok");

        strategy.sendTheRequest(pjp);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(trainingService).selectTraining(idCaptor.capture());
        assertEquals(9L, idCaptor.getValue());
    }
}
