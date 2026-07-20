package com.example.Trainer_history_service.consumers;

import com.example.Trainer_history_service.dto.TrainerWorkloadBatchRequest;
import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.exceptions.CouldNotUpdateTrainerDataException;
import com.example.Trainer_history_service.facade.TrainerFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class TrainerUpdateConsumer {

    private final TrainerFacade trainerFacade;

    @JmsListener(destination = "training-update-channel")
    public void getTrainerUpdateRequest(TrainerWorkloadRequest trainerWorkloadRequest,
                                        @Header("Authorization") String jwtToken) {
        ResponseEntity<String> resp = trainerFacade.updateTrainerWorkload(trainerWorkloadRequest);

        if(resp.getStatusCode() != HttpStatus.OK) throw new CouldNotUpdateTrainerDataException(
                "Could not update trainer data"
        );
    }

    @JmsListener(destination = "training-batch-update-channel")
    public void getTrainerBatchUpdateRequest(TrainerWorkloadBatchRequest trainerWorkloadBatchRequest,
                                             @Header("Authorization") String jwtToken) {
        ResponseEntity<String> resp = trainerFacade.updateTrainersWorkloadInBatch(
                trainerWorkloadBatchRequest.getTrainerWorkloadRequestList());

        if(resp.getStatusCode() != HttpStatus.OK) throw new CouldNotUpdateTrainerDataException(
                "could not update trainer data in batch"
        );
    }

}
