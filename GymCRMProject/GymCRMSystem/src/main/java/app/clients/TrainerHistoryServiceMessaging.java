package app.clients;

import lombok.AllArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TrainerHistoryServiceMessaging {

    private final JmsTemplate jmsTemplate;

    public void sendMessage(String destination, Object payload, String JWTToken, String transactionId) {

            jmsTemplate.convertAndSend(destination, payload, message -> {
                message.setStringProperty("Authorization", JWTToken);
                message.setStringProperty("X-Transaction-ID", transactionId);
                return message;
            });

    }

}
