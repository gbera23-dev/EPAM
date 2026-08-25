package app.unit.MicroserviceInteraction;

import app.messaging.microserviceCommunication.TrainerHistoryServiceCommunication;
import app.api.dto.request.TrainerWorkloadBatchRequest;
import app.api.dto.request.TrainerWorkloadRequest;
import app.domain.entities.ActionType;
import app.domain.entities.Trainer;
import app.domain.entities.Training;
import app.domain.entities.User;
import app.services.business.interfaces.TraineeService;
import app.messaging.strategies.impl.BatchRemoveHoursFromTrainersStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.aopalliance.intercept.MethodInvocation;
import org.jboss.logging.MDC;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchRemoveHoursFromTrainersStrategyTest {

    @Mock private TraineeService traineeService;
    @Mock private TrainerHistoryServiceCommunication trainerHistoryServiceCommunication;
    @Mock private MethodInvocation invocation;
    @Mock private HttpServletRequest httpServletRequest;
    @Mock private Training training;
    @Mock private Trainer trainer;
    @Mock private User user;

    private BatchRemoveHoursFromTrainersStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new BatchRemoveHoursFromTrainersStrategy(traineeService, trainerHistoryServiceCommunication);
        MDC.put("transactionId", "txn-3");
    }

    @Test
    void testSendTheRequestProceedsThenSendsBatchMessage() throws Throwable {
        when(invocation.getArguments()).thenReturn(new Object[]{"trainee.one", httpServletRequest});
        when(traineeService.getAllTrainingsForTrainee("trainee.one")).thenReturn(List.of(training));
        when(training.getTrainer()).thenReturn(trainer);
        when(trainer.getUser()).thenReturn(user);
        when(training.getDate()).thenReturn(java.time.LocalDate.of(2025, 8, 1));
        when(training.getDuration()).thenReturn(30);
        when(user.getUsername()).thenReturn("trainer.one");
        when(user.getFirstName()).thenReturn("Jane");
        when(user.getLastName()).thenReturn("Smith");
        when(user.isActive()).thenReturn(false);
        when(httpServletRequest.getHeader(anyString())).thenReturn("Bearer token");
        when(invocation.proceed()).thenReturn("proceeded");

        Object result = strategy.sendTheRequest(invocation);

        var inOrder = inOrder(invocation, trainerHistoryServiceCommunication);
        inOrder.verify(invocation).proceed();
        ArgumentCaptor<TrainerWorkloadBatchRequest> captor = ArgumentCaptor.forClass(TrainerWorkloadBatchRequest.class);
        inOrder.verify(trainerHistoryServiceCommunication).sendMessage(
                eq("training-batch-update-channel"), captor.capture(), eq("Bearer token"), eq("txn-3"));

        TrainerWorkloadBatchRequest sent = captor.getValue();
        assertEquals(1, sent.getTrainerWorkloadRequestList().size());
        TrainerWorkloadRequest item = sent.getTrainerWorkloadRequestList().get(0);
        assertEquals("trainer.one", item.getUsername());
        assertEquals("Jane", item.getFirstName());
        assertEquals("Smith", item.getLastName());
        assertFalse(item.getIsActive());
        assertEquals(java.time.LocalDate.of(2025, 8, 1), item.getTrainingDate());
        assertEquals(30, item.getDuration());
        assertEquals(ActionType.DELETE, item.getActionType());
        assertEquals("proceeded", result);
    }

    @Test
    void testSendTheRequestLooksUpTrainingsForGivenUsernameBeforeProceeding() throws Throwable {
        when(invocation.getArguments()).thenReturn(new Object[]{"trainee.two", httpServletRequest});
        when(traineeService.getAllTrainingsForTrainee("trainee.two")).thenReturn(List.of());
        when(httpServletRequest.getHeader(anyString())).thenReturn("token");
        when(invocation.proceed()).thenReturn("ok");

        strategy.sendTheRequest(invocation);

        var inOrder = inOrder(traineeService, invocation);
        inOrder.verify(traineeService).getAllTrainingsForTrainee("trainee.two");
        inOrder.verify(invocation).proceed();
    }

    @Test
    void testSendTheRequestWithEmptyTrainingsListStillSendsMessage() throws Throwable {
        when(invocation.getArguments()).thenReturn(new Object[]{"trainee.three", httpServletRequest});
        when(traineeService.getAllTrainingsForTrainee("trainee.three")).thenReturn(List.of());
        when(httpServletRequest.getHeader(anyString())).thenReturn("token");
        when(invocation.proceed()).thenReturn("ok");

        strategy.sendTheRequest(invocation);

        verify(trainerHistoryServiceCommunication).sendMessage(
                eq("training-batch-update-channel"), any(TrainerWorkloadBatchRequest.class), eq("token"), eq("txn-3"));
    }
}
