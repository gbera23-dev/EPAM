package app.unit.MicroserviceInteraction;

import app.messaging.microserviceCommunication.TrainerHistoryServiceCommunication;
import app.api.dto.request.TrainerWorkloadRequest;
import app.api.dto.request.TrainingRequest;
import app.domain.entities.ActionType;
import app.domain.entities.Trainer;
import app.domain.entities.User;
import app.services.business.interfaces.TrainerService;
import app.messaging.strategies.impl.AddHoursToTrainerStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.aopalliance.intercept.MethodInvocation;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddHoursToTrainerStrategyTest {

    @Mock private TrainerService trainerService;
    @Mock private TrainerHistoryServiceCommunication trainerHistoryServiceCommunication;
    @Mock private MethodInvocation invocation;
    @Mock private HttpServletRequest httpServletRequest;
    @Mock private TrainingRequest trainingRequest;
    @Mock private Trainer trainer;
    @Mock private User user;

    private AddHoursToTrainerStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new AddHoursToTrainerStrategy(trainerService, trainerHistoryServiceCommunication);
        MDC.put("transactionId", "txn-1");
    }

    @Test
    void testSendTheRequestBuildsWorkloadRequestAndSendsMessage() throws Throwable {
        when(invocation.getArguments()).thenReturn(new Object[]{trainingRequest, httpServletRequest});
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
        when(invocation.proceed()).thenReturn("proceeded");

        Object result = strategy.sendTheRequest(invocation);

        ArgumentCaptor<TrainerWorkloadRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadRequest.class);
        verify(trainerHistoryServiceCommunication).sendMessage(
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
        verify(invocation).proceed();
    }

    @Test
    void testSendTheRequestSendsMessageBeforeProceeding() throws Throwable {
        when(invocation.getArguments()).thenReturn(new Object[]{trainingRequest, httpServletRequest});
        when(trainingRequest.getTrainerUsername()).thenReturn("trainer.two");
        when(trainerService.selectTrainerProfileByUsername("trainer.two")).thenReturn(trainer);
        when(trainer.getUser()).thenReturn(user);
        when(httpServletRequest.getHeader(anyString())).thenReturn("token");
        when(invocation.proceed()).thenReturn("result");

        strategy.sendTheRequest(invocation);

        var inOrder = inOrder(trainerHistoryServiceCommunication, invocation);
        inOrder.verify(trainerHistoryServiceCommunication).sendMessage(any(), any(), any(), any());
    }
}
