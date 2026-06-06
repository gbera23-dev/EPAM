import dto.internal.TrainingTypeDTO;
import entities.TrainingType;
import mappers.internal.TrainingTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTypeMapperTest {

    private TrainingTypeMapper trainingTypeMapper;

    @BeforeEach
    void setUp() {
        trainingTypeMapper = new TrainingTypeMapper();
    }

    @Test
    void testToDtoMapsId() {
        TrainingType trainingType = buildTrainingType(3L, "yoga");

        TrainingTypeDTO dto = trainingTypeMapper.toDTO(trainingType);

        assertEquals(3L, dto.getId());
    }

    @Test
    void testToDtoMapsName() {
        TrainingType trainingType = buildTrainingType(3L, "yoga");

        TrainingTypeDTO dto = trainingTypeMapper.toDTO(trainingType);

        assertEquals("yoga", dto.getName());
    }

    @Test
    void testToEntitySetsIdToNull() {
        TrainingTypeDTO dto = buildTrainingTypeDTO(7L, "cardio");

        TrainingType trainingType = trainingTypeMapper.toEntity(dto);

        assertNull(trainingType.getId());
    }

    @Test
    void testToEntityMapsName() {
        TrainingTypeDTO dto = buildTrainingTypeDTO(7L, "cardio");

        TrainingType trainingType = trainingTypeMapper.toEntity(dto);

        assertEquals("cardio", trainingType.getName());
    }

    private TrainingType buildTrainingType(Long id, String name) {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(id);
        trainingType.setName(name);
        return trainingType;
    }

    private TrainingTypeDTO buildTrainingTypeDTO(Long id, String name) {
        TrainingTypeDTO dto = new TrainingTypeDTO();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }
}
