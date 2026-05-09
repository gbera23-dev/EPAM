import Builders.TrainerBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Trainer;
import entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
        entry.put("trainerPK", 1);
        entry.put("specialization", "Yoga");
        entry.put("user", null);

        Trainer result = trainerBuilder.build(entry);

        assertEquals(1L, result.getTrainerPK());
    }

    @Test
    void testBuildReturnsTrainerWithCorrectSpecialization() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("trainerPK", 2);
        entry.put("specialization", "Cardio");
        entry.put("user", null);

        Trainer result = trainerBuilder.build(entry);

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
        entry.put("trainerPK", 3);
        entry.put("specialization", "Strength");
        entry.put("user", userMap);

        Trainer result = trainerBuilder.build(entry);

        assertNotNull(result.getUser());
        assertEquals("Jane", result.getUser().getFirstName());
    }

    @Test
    void testBuildHandlesLongPKValue() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("trainerPK", 100L);
        entry.put("specialization", "Pilates");
        entry.put("user", null);

        Trainer result = trainerBuilder.build(entry);

        assertEquals(100L, result.getTrainerPK());
    }
}
