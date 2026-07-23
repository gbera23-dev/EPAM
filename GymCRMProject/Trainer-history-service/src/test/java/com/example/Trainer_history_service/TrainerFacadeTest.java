package com.example.Trainer_history_service;

import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.facade.TrainerFacade;
import com.example.Trainer_history_service.services.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerFacadeTest {

    @Mock private TrainerService trainerService;
    @Mock private TrainerWorkloadRequest trainerWorkloadRequest;

    private TrainerFacade trainerFacade;

    @BeforeEach
    void setUp() {
        trainerFacade = new TrainerFacade(trainerService);
    }

    @Test
    void testUpdateTrainerWorkloadDelegatesToServiceAndReturnsOk() {
        ResponseEntity<String> response = trainerFacade.updateTrainerWorkload(trainerWorkloadRequest);

        verify(trainerService).updateTrainingHours(trainerWorkloadRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("trainer workload updated successfully!", response.getBody());
    }

    @Test
    void testUpdateTrainersWorkloadInBatchDelegatesToServiceAndReturnsOk() {
        List<TrainerWorkloadRequest> requests = List.of(trainerWorkloadRequest);

        ResponseEntity<String> response = trainerFacade.updateTrainersWorkloadInBatch(requests);

        verify(trainerService).updateTrainingHoursInBatch(requests);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Workloads have been updated successfully!", response.getBody());
    }

    @Test
    void testGetTrainerHoursDelegatesToServiceAndReturnsHours() {
        LocalDate date = LocalDate.of(2025, 6, 1);
        when(trainerService.getTrainingHours("trainer.one", date)).thenReturn(42);

        ResponseEntity<Integer> response = trainerFacade.getTrainerHours("trainer.one", date);

        verify(trainerService).getTrainingHours("trainer.one", date);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(42, response.getBody());
    }
}
