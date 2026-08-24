package app.unit.builders;

import app.builders.TrainerBuilder;
import app.dto.internal.TrainerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TrainerBuilderTest {

    private TrainerBuilder trainerBuilder;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        trainerBuilder = new TrainerBuilder(objectMapper);
    }

    @Test
    void testBuildReturnsTrainerWithCorrectPK() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("trainerPk", 1);
        entry.put("specialization", "Yoga");
        entry.put("user", null);

        TrainerDTO result = trainerBuilder.build(entry);

        assertEquals(1L, result.getTrainerPk());
    }

    @Test
    void testBuildReturnsTrainerWithCorrectSpecialization() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("trainerPk", 2);
        entry.put("specialization", "Cardio");
        entry.put("user", null);

        TrainerDTO result = trainerBuilder.build(entry);

        assertEquals("Cardio", result.getSpecialization());
    }

    @Test
    void testBuildReturnsTrainerWithUserObject() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", 5);
        userMap.put("firstName", "Jane");
        userMap.put("lastName", "Smith");
        userMap.put("username", "jane.smith");
        userMap.put("password", "secret");
        userMap.put("isActive", true);

        Map<String, Object> entry = new HashMap<>();
        entry.put("trainerPk", 3);
        entry.put("specialization", "Strength");
        entry.put("user", userMap);

        TrainerDTO result = trainerBuilder.build(entry);

        assertNotNull(result.getUser());
        assertEquals("Jane", result.getUser().getFirstName());
    }

    @Test
    void testBuildHandlesLongPKValue() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("trainerPk", 100L);
        entry.put("specialization", "Pilates");
        entry.put("user", null);

        TrainerDTO result = trainerBuilder.build(entry);

        assertEquals(100L, result.getEntityId());
    }
}
