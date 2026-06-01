import builders.TraineeBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.TraineeDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TraineeBuilderTest {

    private TraineeBuilder traineeBuilder;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        traineeBuilder = new TraineeBuilder(objectMapper);
    }

    @Test
    void testBuildReturnsTraineeWithCorrectPK() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("traineePK", 1);
        entry.put("dateOfBirth", null);
        entry.put("address", "123 Main St");
        entry.put("user", null);

        TraineeDTO result = traineeBuilder.build(entry);

        assertEquals(1L, result.getTraineePk());
    }

    @Test
    void testBuildReturnsTraineeWithCorrectAddress() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("traineePK", 2);
        entry.put("dateOfBirth", null);
        entry.put("address", "456 Elm St");
        entry.put("user", null);

        TraineeDTO result = traineeBuilder.build(entry);

        assertEquals("456 Elm St", result.getAddress());
    }

    @Test
    void testBuildReturnsTraineeWithUserObject() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", 10);
        userMap.put("firstName", "John");
        userMap.put("lastName", "Doe");
        userMap.put("username", "john.doe");
        userMap.put("password", "pass123");
        userMap.put("isActive", true);

        Map<String, Object> entry = new HashMap<>();
        entry.put("traineePK", 3);
        entry.put("dateOfBirth", null);
        entry.put("address", "789 Oak Ave");
        entry.put("user", userMap);

        TraineeDTO result = traineeBuilder.build(entry);

        assertNotNull(result.getUser());
        assertEquals("John", result.getUser().getFirstName());
    }

    @Test
    void testBuildHandlesLongValueForPK() {
        Map<String, Object> entry = new HashMap<>();
        entry.put("traineePK", 999999999L);
        entry.put("dateOfBirth", null);
        entry.put("address", "Test");
        entry.put("user", null);

        TraineeDTO result = traineeBuilder.build(entry);

        assertEquals(999999999L, result.getTraineePk());
    }

}
