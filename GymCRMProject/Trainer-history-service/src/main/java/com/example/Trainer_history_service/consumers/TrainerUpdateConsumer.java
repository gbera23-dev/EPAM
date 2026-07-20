package com.example.Trainer_history_service.consumers;

import com.example.Trainer_history_service.dto.TrainerWorkloadBatchRequest;
import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.facade.TrainerFacade;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class TrainerUpdateConsumer {

    private final TrainerFacade trainerFacade;
    private final ObjectMapper objectMapper;

    @JmsListener(destination = "training-update-channel")
    public void getTrainerUpdateRequest(TrainerWorkloadRequest trainerWorkloadRequest,
                                        @Header("Authorization") String jwtToken) {
        ResponseEntity<String> res = null;
        try {
            res = trainerFacade.updateTrainerWorkload(trainerWorkloadRequest);
        } catch(Exception e) {
            log.error("Messaging request failed!");
            log.error("{} {}", trainerWorkloadRequest, jwtToken);
            log.error("exception thrown was: {}", e.getMessage());
        }
        finally {
            if(res != null) log.info("result is {}", res.getBody());
        }
    }

    @JmsListener(destination = "training-batch-update-channel")
    public void getTrainerBatchUpdateRequest(TrainerWorkloadBatchRequest trainerWorkloadBatchRequest,
                                             @Header("Authorization") String jwtToken) {
        ResponseEntity<String> res = null;
        try {
            res = trainerFacade.updateTrainersWorkloadInBatch(
                    trainerWorkloadBatchRequest.getTrainerWorkloadRequestList());
        } catch(Exception e) {
            log.error("Messaging request failed!");
        }
        finally {
            if(res != null) log.info("result is {}", res.getBody());
        }
    }

}
