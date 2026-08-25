package com.example.Trainer_history_service.api.consumers;

import com.example.Trainer_history_service.aop.annotations.ConsumerLayer;
import com.example.Trainer_history_service.api.dto.TrainerWorkloadBatchRequest;
import com.example.Trainer_history_service.api.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.api.exceptions.CouldNotUpdateTrainerDataException;
import com.example.Trainer_history_service.api.facade.TrainerFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static com.example.Trainer_history_service.infrastructure.constants.MessagingConstants.TRAINING_BATCH_UPDATE_CHANNEL;
import static com.example.Trainer_history_service.infrastructure.constants.MessagingConstants.TRAINING_UPDATE_CHANNEL;
import static com.example.Trainer_history_service.infrastructure.constants.SecurityConstants.AUTHORIZATION_HEADER;
import static com.example.Trainer_history_service.infrastructure.constants.TransactionConstants.TRANSACTION_HEADER_NAME;

@Component
@Slf4j
@AllArgsConstructor
@ConsumerLayer
public class TrainerUpdateConsumer {

    private final TrainerFacade trainerFacade;

    @JmsListener(destination = TRAINING_UPDATE_CHANNEL)
    public void getTrainerUpdateRequest(TrainerWorkloadRequest trainerWorkloadRequest,
                                        @Header(AUTHORIZATION_HEADER) String jwtToken,
                                        @Header(TRANSACTION_HEADER_NAME) String transactionId) {
        ResponseEntity<String> resp = trainerFacade.updateTrainerWorkload(trainerWorkloadRequest);

        if(resp.getStatusCode() != HttpStatus.OK) throw new CouldNotUpdateTrainerDataException(
                "Could not update trainer data"
        );
    }

    @JmsListener(destination = TRAINING_BATCH_UPDATE_CHANNEL)
    public void getTrainerBatchUpdateRequest(TrainerWorkloadBatchRequest trainerWorkloadBatchRequest,
                                             @Header(AUTHORIZATION_HEADER) String jwtToken,
                                             @Header(TRANSACTION_HEADER_NAME) String transactionId) {
        ResponseEntity<String> resp = trainerFacade.updateTrainersWorkloadInBatch(
                trainerWorkloadBatchRequest.getTrainerWorkloadRequestList());

        if(resp.getStatusCode() != HttpStatus.OK) throw new CouldNotUpdateTrainerDataException(
                "could not update trainer data in batch"
        );
    }

}
