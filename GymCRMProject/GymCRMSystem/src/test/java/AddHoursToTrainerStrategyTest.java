import app.clients.TrainerHistoryServiceMessaging;
import app.dto.api.request.TrainerWorkloadRequest;
import app.dto.api.request.TrainingRequest;
import app.entities.ActionType;
import app.entities.Trainer;
import app.entities.User;
import app.services.TrainerService;
import app.strategies.MicroserviceInteraction.AddHoursToTrainerStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddHoursToTrainerStrategyTest {

    @Mock private TrainerService trainerService;
    @Mock private TrainerHistoryServiceMessaging trainerHistoryServiceMessaging;
    @Mock private ProceedingJoinPoint pjp;
    @Mock private HttpServletRequest httpServletRequest;
    @Mock private TrainingRequest trainingRequest;
    @Mock private Trainer trainer;
    @Mock private User user;

    private AddHoursToTrainerStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new AddHoursToTrainerStrategy(trainerService, trainerHistoryServiceMessaging);
        MDC.put("transactionId", "txn-1");
    }

    @Test
    void testSendTheRequestBuildsWorkloadRequestAndSendsMessage() throws Throwable {
        when(pjp.getArgs()).thenReturn(new Object[]{trainingRequest, httpServletRequest});
        when(trainingRequest.getTrainerUsername()).thenReturn("trainer.one");
        when(trainingRequest.getDate()).thenReturn(LocalDate.of(2025, 6, 1));
        when(trainingRequest.getDuration()).thenReturn(60);
        when(trainerService.selectTrainerProfileByUsername("trainer.one")).thenReturn(trainer);
        when(trainer.getUser()).thenReturn(user);
        when(user.getUsername()).thenReturn("trainer.one");
        when(user.getFirstName()).thenReturn("John");
        when(user.getLastName()).thenReturn("Doe");
        when(user.isActive()).thenReturn(true);
        when(httpServletRequest.getHeader(anyString())).thenReturn("Bearer token");
        when(pjp.proceed()).thenReturn("proceeded");

        Object result = strategy.sendTheRequest(pjp);

        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(trainerHistoryServiceMessaging).sendMessage(
                eq("training-update-channel"), captor.capture(), eq("Bearer token"), eq("txn-1"));

        TrainerWorkloadRequest sent = captor.getValue();
        assertEquals("trainer.one", sent.getUsername());
        assertEquals("John", sent.getFirstName());
        assertEquals("Doe", sent.getLastName());
        assertTrue(sent.getIsActive());
        assertEquals(LocalDate.of(2025, 6, 1), sent.getTrainingDate());
        assertEquals(60, sent.getDuration());
        assertEquals(ActionType.ADD, sent.getActionType());
        assertEquals("proceeded", result);
        verify(pjp).proceed();
    }

    @Test
    void testSendTheRequestSendsMessageBeforeProceeding() throws Throwable {
        when(pjp.getArgs()).thenReturn(new Object[]{trainingRequest, httpServletRequest});
        when(trainingRequest.getTrainerUsername()).thenReturn("trainer.two");
        when(trainerService.selectTrainerProfileByUsername("trainer.two")).thenReturn(trainer);
        when(trainer.getUser()).thenReturn(user);
        when(httpServletRequest.getHeader(anyString())).thenReturn("token");
        when(pjp.proceed()).thenReturn("result");

        strategy.sendTheRequest(pjp);

        var inOrder = inOrder(trainerHistoryServiceMessaging, pjp);
        inOrder.verify(trainerHistoryServiceMessaging).sendMessage(any(), any(), any(), any());
        inOrder.verify(pjp).proceed();
    }
}
