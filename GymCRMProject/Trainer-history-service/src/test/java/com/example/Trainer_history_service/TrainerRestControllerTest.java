package com.example.Trainer_history_service;

import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.entities.ActionType;
import com.example.Trainer_history_service.restController.TrainerRestController;
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
class TrainerRestControllerTest {

    @Mock
    private TrainerService trainerService;

    private TrainerRestController controller;

    @BeforeEach
    void setUp() {
        controller = new TrainerRestController(trainerService);
    }

    @Test
    void testUpdateTrainerWorkloadCallsAddTrainingHoursWhenActionTypeAdd() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "john", "John", "Doe", true, LocalDate.of(2026, 7, 1), 60, ActionType.ADD);

        ResponseEntity<String> response = controller.updateTrainerWorkload(request);

        verify(trainerService).addTrainingHours("john", LocalDate.of(2026, 7, 1), 60);
        verify(trainerService, never()).deleteTrainingHours(anyString(), any(), anyInt());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateTrainerWorkloadCallsDeleteTrainingHoursWhenActionTypeNotAdd() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "john", "John", "Doe", true, LocalDate.of(2026, 7, 1), 60, ActionType.DELETE);

        ResponseEntity<String> response = controller.updateTrainerWorkload(request);

        verify(trainerService).deleteTrainingHours("john", LocalDate.of(2026, 7, 1), 60);
        verify(trainerService, never()).addTrainingHours(anyString(), any(), anyInt());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateTrainerWorkloadCreatesWorkloadWhenNotExists() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "john", "John", "Doe", true, LocalDate.of(2026, 7, 1), 60, ActionType.ADD);
        when(trainerService.workloadExists("john")).thenReturn(false);

        controller.updateTrainerWorkload(request);

        verify(trainerService).createNewWorkload("john", "John", "Doe", true);
    }

    @Test
    void testUpdateTrainerWorkloadDoesNotCreateWorkloadWhenExists() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "john", "John", "Doe", true, LocalDate.of(2026, 7, 1), 60, ActionType.ADD);
        when(trainerService.workloadExists("john")).thenReturn(true);

        controller.updateTrainerWorkload(request);

        verify(trainerService, never()).createNewWorkload(anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    void testUpdateTrainersWorkloadInBatchCallsAddTrainingHoursForAddRequest() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "john", "John", "Doe", true, LocalDate.of(2026, 7, 1), 60, ActionType.ADD);
        when(trainerService.workloadExists("john")).thenReturn(true);

        ResponseEntity<String> response = controller.updateTrainersWorkloadInBatch(List.of(request));

        verify(trainerService).addTrainingHours("john", LocalDate.of(2026, 7, 1), 60);
        verify(trainerService, never()).deleteTrainingHours(anyString(), any(), anyInt());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateTrainersWorkloadInBatchCallsDeleteTrainingHoursForDeleteRequest() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "john", "John", "Doe", true, LocalDate.of(2026, 7, 1), 60, ActionType.DELETE);
        when(trainerService.workloadExists("john")).thenReturn(true);

        ResponseEntity<String> response = controller.updateTrainersWorkloadInBatch(List.of(request));

        verify(trainerService).deleteTrainingHours("john", LocalDate.of(2026, 7, 1), 60);
        verify(trainerService, never()).addTrainingHours(anyString(), any(), anyInt());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateTrainersWorkloadInBatchCreatesWorkloadWhenNotExists() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "john", "John", "Doe", true, LocalDate.of(2026, 7, 1), 60, ActionType.ADD);
        when(trainerService.workloadExists("john")).thenReturn(false);

        controller.updateTrainersWorkloadInBatch(List.of(request));

        verify(trainerService).createNewWorkload("john", "John", "Doe", true);
    }

    @Test
    void testUpdateTrainersWorkloadInBatchDoesNotCreateWorkloadWhenExists() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "john", "John", "Doe", true, LocalDate.of(2026, 7, 1), 60, ActionType.ADD);
        when(trainerService.workloadExists("john")).thenReturn(true);

        controller.updateTrainersWorkloadInBatch(List.of(request));

        verify(trainerService, never()).createNewWorkload(anyString(), anyString(), anyString(), anyBoolean());
    }

    @Test
    void testUpdateTrainersWorkloadInBatchEmptyListReturnsOk() {
        ResponseEntity<String> response = controller.updateTrainersWorkloadInBatch(List.of());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verifyNoInteractions(trainerService);
    }

    @Test
    void testGetTrainerHoursReturnsHoursFromService() {
        when(trainerService.getTrainingHours("john", LocalDate.of(2026, 7, 1))).thenReturn(120);

        ResponseEntity<Integer> response = controller.getTrainerHours("john", LocalDate.of(2026, 7, 1));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(120, response.getBody());
    }
}