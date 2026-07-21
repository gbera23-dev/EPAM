package com.example.Trainer_history_service;

import com.example.Trainer_history_service.consumers.TrainerUpdateConsumer;
import com.example.Trainer_history_service.dto.TrainerWorkloadBatchRequest;
import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.exceptions.CouldNotUpdateTrainerDataException;
import com.example.Trainer_history_service.facade.TrainerFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerUpdateConsumerTest {

    @Mock private TrainerFacade trainerFacade;
    @Mock private TrainerWorkloadRequest trainerWorkloadRequest;
    @Mock private TrainerWorkloadBatchRequest trainerWorkloadBatchRequest;

    private TrainerUpdateConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new TrainerUpdateConsumer(trainerFacade);
    }

    @Test
    void testGetTrainerUpdateRequestDelegatesToFacadeOnSuccess() {
        when(trainerFacade.updateTrainerWorkload(trainerWorkloadRequest))
                .thenReturn(ResponseEntity.ok("trainer workload updated successfully!"));

        assertDoesNotThrow(() -> consumer.getTrainerUpdateRequest(trainerWorkloadRequest, "Bearer token", "txn-1"));

        verify(trainerFacade).updateTrainerWorkload(trainerWorkloadRequest);
    }

    @Test
    void testGetTrainerUpdateRequestThrowsWhenFacadeReturnsNonOkStatus() {
        when(trainerFacade.updateTrainerWorkload(trainerWorkloadRequest))
                .thenReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed"));

        assertThrows(CouldNotUpdateTrainerDataException.class,
                () -> consumer.getTrainerUpdateRequest(trainerWorkloadRequest, "Bearer token", "txn-1"));
    }

    @Test
    void testGetTrainerBatchUpdateRequestDelegatesToFacadeOnSuccess() {
        List<TrainerWorkloadRequest> requests = List.of(trainerWorkloadRequest);
        when(trainerWorkloadBatchRequest.getTrainerWorkloadRequestList()).thenReturn(requests);
        when(trainerFacade.updateTrainersWorkloadInBatch(requests))
                .thenReturn(ResponseEntity.ok("Workloads have been updated successfully!"));

        assertDoesNotThrow(() -> consumer.getTrainerBatchUpdateRequest(trainerWorkloadBatchRequest, "Bearer token", "txn-2"));

        verify(trainerFacade).updateTrainersWorkloadInBatch(requests);
    }

    @Test
    void testGetTrainerBatchUpdateRequestThrowsWhenFacadeReturnsNonOkStatus() {
        List<TrainerWorkloadRequest> requests = List.of(trainerWorkloadRequest);
        when(trainerWorkloadBatchRequest.getTrainerWorkloadRequestList()).thenReturn(requests);
        when(trainerFacade.updateTrainersWorkloadInBatch(requests))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("failed"));

        assertThrows(CouldNotUpdateTrainerDataException.class,
                () -> consumer.getTrainerBatchUpdateRequest(trainerWorkloadBatchRequest, "Bearer token", "txn-2"));
    }
}
