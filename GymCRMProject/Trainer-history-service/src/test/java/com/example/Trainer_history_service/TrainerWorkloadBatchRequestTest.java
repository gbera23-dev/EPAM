package com.example.Trainer_history_service;

import com.example.Trainer_history_service.dto.TrainerWorkloadBatchRequest;
import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.entities.ActionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainerWorkloadBatchRequestTest {

    @Test
    void testAllArgsConstructorSetsTrainerWorkloadRequestList() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.one", "John", "Doe", true, LocalDate.of(2025, 6, 1), 60, ActionType.ADD);
        List<TrainerWorkloadRequest> list = List.of(request);

        TrainerWorkloadBatchRequest batchRequest = new TrainerWorkloadBatchRequest(list);

        assertEquals(list, batchRequest.getTrainerWorkloadRequestList());
        assertEquals(1, batchRequest.getTrainerWorkloadRequestList().size());
    }

    @Test
    void testNoArgsConstructorAndSetterSetTrainerWorkloadRequestList() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        List<TrainerWorkloadRequest> list = List.of(request);
        TrainerWorkloadBatchRequest batchRequest = new TrainerWorkloadBatchRequest();

        batchRequest.setTrainerWorkloadRequestList(list);

        assertEquals(list, batchRequest.getTrainerWorkloadRequestList());
    }

    @Test
    void testNoArgsConstructorLeavesListNull() {
        TrainerWorkloadBatchRequest batchRequest = new TrainerWorkloadBatchRequest();

        assertNull(batchRequest.getTrainerWorkloadRequestList());
    }

    @Test
    void testSetTrainerWorkloadRequestListAcceptsEmptyList() {
        TrainerWorkloadBatchRequest batchRequest = new TrainerWorkloadBatchRequest();

        batchRequest.setTrainerWorkloadRequestList(List.of());

        assertTrue(batchRequest.getTrainerWorkloadRequestList().isEmpty());
    }
}
