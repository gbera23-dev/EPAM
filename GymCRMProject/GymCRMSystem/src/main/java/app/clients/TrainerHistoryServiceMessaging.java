package app.clients;

import lombok.AllArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import static app.utils.SecurityConstants.AUTHORIZATION_HEADER;
import static app.utils.TransactionConstants.TRANSACTION_HEADER_NAME;

@AllArgsConstructor
@Service
public class TrainerHistoryServiceMessaging {

    private final JmsTemplate jmsTemplate;

    public void sendMessage(String destination, Object payload, String JWTToken, String transactionId) {

            jmsTemplate.convertAndSend(destination, payload, message -> {
                message.setStringProperty(AUTHORIZATION_HEADER, JWTToken);
                message.setStringProperty(TRANSACTION_HEADER_NAME, transactionId);
                return message;
            });

    }

}
