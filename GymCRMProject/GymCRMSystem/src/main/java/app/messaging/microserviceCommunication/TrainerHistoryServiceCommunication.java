package app.messaging.microserviceCommunication;

import app.aop.annotations.ClientLayer;
import lombok.AllArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import static app.infrastructure.constants.SecurityConstants.AUTHORIZATION_HEADER;
import static app.infrastructure.constants.TransactionConstants.TRANSACTION_HEADER_NAME;

@AllArgsConstructor
@Service
@ClientLayer
public class TrainerHistoryServiceCommunication {

    private final JmsTemplate jmsTemplate;

    public void sendMessage(String destination, Object payload, String JWTToken, String transactionId) {

            jmsTemplate.convertAndSend(destination, payload, message -> {
                message.setStringProperty(AUTHORIZATION_HEADER, JWTToken);
                message.setStringProperty(TRANSACTION_HEADER_NAME, transactionId);
                return message;
            });

    }

}
