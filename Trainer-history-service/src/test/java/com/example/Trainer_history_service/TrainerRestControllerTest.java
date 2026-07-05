package com.example.Trainer_history_service;

import com.example.Trainer_history_service.dto.TrainerHoursRequest;
import com.example.Trainer_history_service.dto.TrainerWorkloadCreationRequest;
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
    void testGetTrainerHoursReturnsHours() {
        TrainerHoursRequest request = new TrainerHoursRequest("john", LocalDate.of(2026, 7, 1));
        when(trainerService.getTrainingHours("john", LocalDate.of(2026, 7, 1))).thenReturn(100);

        ResponseEntity<Integer> response = controller.getTrainerHours(request);

        assertEquals(100, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
