import app.clients.TrainerHistoryServiceMessaging;
import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerHistoryServiceMessagingTest {

    @Mock private JmsTemplate jmsTemplate;
    @Mock private Message message;

    private TrainerHistoryServiceMessaging messaging;

    @BeforeEach
    void setUp() {
        messaging = new TrainerHistoryServiceMessaging(jmsTemplate);
    }

    @Test
    void testSendMessageDelegatesToJmsTemplateWithGivenDestinationAndPayload() {
        Object payload = new Object();

        messaging.sendMessage("training-update-channel", payload, "Bearer token", "txn-1");

        verify(jmsTemplate).convertAndSend(eq("training-update-channel"), eq(payload), any(MessagePostProcessor.class));
    }

    @Test
    void testSendMessagePostProcessorSetsAuthorizationAndTransactionIdHeaders() throws Exception {
        Object payload = new Object();

        messaging.sendMessage("training-update-channel", payload, "Bearer token", "txn-42");

        ArgumentCaptor<MessagePostProcessor> captor = ArgumentCaptor.forClass(MessagePostProcessor.class);
        verify(jmsTemplate).convertAndSend(eq("training-update-channel"), eq(payload), captor.capture());

        captor.getValue().postProcessMessage(message);

        verify(message).setStringProperty("Authorization", "Bearer token");
        verify(message).setStringProperty("X-Transaction-ID", "txn-42");
    }
}
