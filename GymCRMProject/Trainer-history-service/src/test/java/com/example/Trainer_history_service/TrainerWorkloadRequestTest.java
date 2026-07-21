package com.example.Trainer_history_service;

import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.entities.ActionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainerWorkloadRequestTest {

    @Test
    void testAllArgsConstructorSetsAllFields() {
        LocalDate date = LocalDate.of(2025, 6, 1);

        TrainerWorkloadRequest request = new TrainerWorkloadRequest(
                "trainer.one", "John", "Doe", true, date, 60, ActionType.ADD);

        assertEquals("trainer.one", request.getUsername());
        assertEquals("John", request.getFirstName());
        assertEquals("Doe", request.getLastName());
        assertTrue(request.getIsActive());
        assertEquals(date, request.getTrainingDate());
        assertEquals(60, request.getDuration());
        assertEquals(ActionType.ADD, request.getActionType());
    }

    @Test
    void testNoArgsConstructorAndSettersSetAllFields() {
        LocalDate date = LocalDate.of(2025, 7, 15);
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        request.setUsername("trainer.two");
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setIsActive(false);
        request.setTrainingDate(date);
        request.setDuration(45);
        request.setActionType(ActionType.DELETE);

        assertEquals("trainer.two", request.getUsername());
        assertEquals("Jane", request.getFirstName());
        assertEquals("Smith", request.getLastName());
        assertFalse(request.getIsActive());
        assertEquals(date, request.getTrainingDate());
        assertEquals(45, request.getDuration());
        assertEquals(ActionType.DELETE, request.getActionType());
    }

    @Test
    void testNoArgsConstructorLeavesFieldsNull() {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();

        assertNull(request.getUsername());
        assertNull(request.getFirstName());
        assertNull(request.getLastName());
        assertNull(request.getIsActive());
        assertNull(request.getTrainingDate());
        assertNull(request.getDuration());
        assertNull(request.getActionType());
    }
}
