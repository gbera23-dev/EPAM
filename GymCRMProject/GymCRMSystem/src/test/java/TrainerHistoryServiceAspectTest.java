import app.annotations.InteractsWithTraineeHistoryService;
import app.aspects.microserviceInteraction.TrainerHistoryServiceAspect;
import app.strategies.MicroserviceInteraction.MicroserviceInteractionStrategy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerHistoryServiceAspectTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ProceedingJoinPoint pjp;

    @Mock
    private InteractsWithTraineeHistoryService interactsWithTraineeHistoryService;

    @Mock
    private MicroserviceInteractionStrategy strategy;

    @InjectMocks
    private TrainerHistoryServiceAspect aspect;

    @Test
    void testSendRequestToMicroserviceOnValidAnnotation() throws Throwable {
        when(interactsWithTraineeHistoryService.chosenStrategy())
                .thenReturn((Class) MicroserviceInteractionStrategy.class);
        when(applicationContext.getBean(MicroserviceInteractionStrategy.class))
                .thenReturn(strategy);
        when(strategy.sendTheRequest(pjp)).thenReturn("result");

        Object result = aspect.sendRequestToMicroservice(pjp, interactsWithTraineeHistoryService);

        assertEquals("result", result);
        verify(strategy, times(1)).sendTheRequest(pjp);
    }

    @Test
    void testSendRequestToMicroserviceOnStrategyThrowsException() throws Throwable {
        when(interactsWithTraineeHistoryService.chosenStrategy())
                .thenReturn((Class) MicroserviceInteractionStrategy.class);
        when(applicationContext.getBean(MicroserviceInteractionStrategy.class))
                .thenReturn(strategy);
        when(strategy.sendTheRequest(pjp)).thenThrow(new RuntimeException("strategy failed"));

        assertThrows(RuntimeException.class,
                () -> aspect.sendRequestToMicroservice(pjp, interactsWithTraineeHistoryService));
    }

    @Test
    void testSendRequestToMicroserviceOnBeanNotFound() {
        when(interactsWithTraineeHistoryService.chosenStrategy())
                .thenReturn((Class) MicroserviceInteractionStrategy.class);
        when(applicationContext.getBean(MicroserviceInteractionStrategy.class))
                .thenThrow(new RuntimeException("No bean found"));

        assertThrows(RuntimeException.class,
                () -> aspect.sendRequestToMicroservice(pjp, interactsWithTraineeHistoryService));

        verifyNoInteractions(strategy);
    }

    @Test
    void testSendRequestToMicroserviceOnNullReturnFromStrategy() throws Throwable {
        when(interactsWithTraineeHistoryService.chosenStrategy())
                .thenReturn((Class) MicroserviceInteractionStrategy.class);
        when(applicationContext.getBean(MicroserviceInteractionStrategy.class))
                .thenReturn(strategy);
        when(strategy.sendTheRequest(pjp)).thenReturn(null);

        Object result = aspect.sendRequestToMicroservice(pjp, interactsWithTraineeHistoryService);

        assertNull(result);
    }
}
