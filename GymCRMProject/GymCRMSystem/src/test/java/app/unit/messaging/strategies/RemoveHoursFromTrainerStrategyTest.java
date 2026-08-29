package app.unit.messaging.strategies;

import app.messaging.microserviceCommunication.TrainerHistoryServiceCommunication;
import app.api.dto.request.TrainerWorkloadRequest;
import app.domain.entities.ActionType;
import app.domain.entities.Trainer;
import app.domain.entities.Training;
import app.domain.entities.User;
import app.services.business.interfaces.TrainingService;
import app.messaging.strategies.impl.RemoveHoursFromTrainerStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.aopalliance.intercept.MethodInvocation;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveHoursFromTrainerStrategyTest {

    @Mock private TrainingService trainingService;
    @Mock private TrainerHistoryServiceCommunication trainerHistoryServiceCommunication;
    @Mock private MethodInvocation invocation;
    @Mock private HttpServletRequest httpServletRequest;
    @Mock private Training training;
    @Mock private Trainer trainer;
    @Mock private User user;

    private RemoveHoursFromTrainerStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new RemoveHoursFromTrainerStrategy(trainingService, trainerHistoryServiceCommunication);
        MDC.put("transactionId", "txn-2");
    }

    @Test
    void testSendTheRequestProceedsThenSendsMessage() throws Throwable {
        when(invocation.getArguments()).thenReturn(new Object[]{5L, httpServletRequest});
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
        when(invocation.proceed()).thenReturn("proceeded");

        Object result = strategy.sendTheRequest(invocation);

        var inOrder = inOrder(invocation, trainerHistoryServiceCommunication);
        inOrder.verify(invocation).proceed();
        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        inOrder.verify(trainerHistoryServiceCommunication).sendMessage(
                eq("training-update-channel"), captor.capture(), eq("Bearer token"), eq("txn-2"));

        TrainerWorkloadRequest sent = captor.getValue();
        assertEquals("trainer.one", sent.getUsername());
        assertEquals("John", sent.getFirstName());
        assertEquals("Doe", sent.getLastName());
        assertTrue(sent.getIsActive());
        assertEquals(java.time.LocalDate.of(2025, 7, 1), sent.getTrainingDate());
        assertEquals(45, sent.getDuration());
        assertEquals(ActionType.DELETE, sent.getActionType());
        assertEquals("proceeded", result);
    }

    @Test
    void testSendTheRequestUsesTrainingIdFromArgsToLookUpTraining() throws Throwable {
        when(invocation.getArguments()).thenReturn(new Object[]{9L, httpServletRequest});
        when(trainingService.selectTraining(9L)).thenReturn(training);
        when(training.getTrainer()).thenReturn(trainer);
        when(trainer.getUser()).thenReturn(user);
        when(httpServletRequest.getHeader(anyString())).thenReturn("token");
        when(invocation.proceed()).thenReturn("ok");

        strategy.sendTheRequest(invocation);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(trainingService).selectTraining(idCaptor.capture());
        assertEquals(9L, idCaptor.getValue());
    }
}
