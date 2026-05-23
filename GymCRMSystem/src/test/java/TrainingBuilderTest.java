import builders.TrainingBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrainingBuilderTest {

    private TrainingBuilder trainingBuilder;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        trainingBuilder = new TrainingBuilder(objectMapper);
    }

    @Test
    void testBuildReturnsTrainingWithCorrectPK() {
        Map<String, Object> entry = buildBaseEntry(1, 10, 20, "Morning Run", 60);

        Training result = trainingBuilder.build(entry);

        assertEquals(1L, result.getId());
    }

    @Test
    void testBuildReturnsTrainingWithCorrectTraineeId() {
        Map<String, Object> entry = buildBaseEntry(2, 15, 25, "Evening Swim", 45);

        Training result = trainingBuilder.build(entry);

        assertEquals(15L, result.getTrainee().getId());
    }

    @Test
    void testBuildReturnsTrainingWithCorrectTrainerId() {
        Map<String, Object> entry = buildBaseEntry(3, 5, 8, "Weightlifting", 90);

        Training result = trainingBuilder.build(entry);

        assertEquals(8L, result.getTrainer().getId());
    }

    @Test
    void testBuildReturnsTrainingWithCorrectName() {
        Map<String, Object> entry = buildBaseEntry(4, 1, 2, "Yoga Session", 30);

        Training result = trainingBuilder.build(entry);

        assertEquals("Yoga Session", result.getName());
    }

    @Test
    void testBuildReturnsTrainingWithCorrectDuration() {
        Map<String, Object> entry = buildBaseEntry(5, 3, 6, "HIIT", 120);

        Training result = trainingBuilder.build(entry);

        assertEquals(120, result.getDuration());
    }

    @Test
    void testBuildReturnsTrainingWithTrainingType() {
        Map<String, Object> trainingTypeMap = new HashMap<>();
        trainingTypeMap.put("id", 1);
        trainingTypeMap.put("name", "Cardio");

        Map<String, Object> entry = buildBaseEntry(6, 2, 4, "Bike Ride", 60);
        entry.put("trainingType", trainingTypeMap);

        Training result = trainingBuilder.build(entry);

        assertNotNull(result.getTrainingType());
        assertEquals("Cardio", result.getTrainingType().getName());
    }

    private Map<String, Object> buildBaseEntry(int pk, int traineeId, int trainerId, String name, int duration) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("trainingPK", pk);
        entry.put("traineeId", traineeId);
        entry.put("trainerId", trainerId);
        entry.put("name", name);
        entry.put("trainingType", null);
        entry.put("date", null);
        entry.put("duration", duration);
        return entry;
    }
}
