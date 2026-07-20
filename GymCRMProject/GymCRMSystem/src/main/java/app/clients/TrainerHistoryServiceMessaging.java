package app.clients;
import app.dto.api.request.TrainerWorkloadRequest;
import jakarta.jms.TextMessage;
import lombok.AllArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class TrainerHistoryServiceMessaging {

    private final JmsTemplate jmsTemplate;

    public void sendMessage(String destination, Object payload, String JWTToken) {

            jmsTemplate.convertAndSend(destination, payload, message -> {
                message.setStringProperty("Authorization", JWTToken);
                return message;
            });

    }

}
