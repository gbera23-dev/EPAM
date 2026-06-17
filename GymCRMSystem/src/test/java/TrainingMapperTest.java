import app.dto.internal.TrainingDTO;
import app.dto.internal.TrainingTypeDTO;
import app.entities.Trainee;
import app.entities.Trainer;
import app.entities.Training;
import app.entities.TrainingType;
import app.mappers.internal.TrainingMapper;
import app.mappers.internal.TrainingTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingMapperTest {

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @InjectMocks
    private TrainingMapper trainingMapper;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;
    private TrainingTypeDTO trainingTypeDTO;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainee.setId(10L);

        trainer = new Trainer();
        trainer.setId(20L);

        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setName("yoga");

        trainingTypeDTO = new TrainingTypeDTO();
        trainingTypeDTO.setId(1L);
        trainingTypeDTO.setName("yoga");
    }

    @Test
    void testToDtoMapsTrainingPk() {
        Training training = buildTraining(5L, trainee, trainer, "Morning Run",
                trainingType, LocalDate.of(2024, 4, 10), 60);
        when(trainingTypeMapper.toDTO(trainingType)).thenReturn(trainingTypeDTO);

        TrainingDTO dto = trainingMapper.toDTO(training);

        assertEquals(5L, dto.getEntityId());
    }

    @Test
    void testToDtoMapsTraineeId() {
        Training training = buildTraining(5L, trainee, trainer, "Morning Run",
                trainingType, LocalDate.of(2024, 4, 10), 60);
        when(trainingTypeMapper.toDTO(trainingType)).thenReturn(trainingTypeDTO);

        TrainingDTO dto = trainingMapper.toDTO(training);

        assertEquals(10L, dto.getTraineeId());
    }

    @Test
    void testToDtoMapsTrainerId() {
        Training training = buildTraining(5L, trainee, trainer, "Morning Run",
                trainingType, LocalDate.of(2024, 4, 10), 60);
        when(trainingTypeMapper.toDTO(trainingType)).thenReturn(trainingTypeDTO);

        TrainingDTO dto = trainingMapper.toDTO(training);

        assertEquals(20L, dto.getTrainerId());
    }

    @Test
    void testToDtoMapsName() {
        Training training = buildTraining(5L, trainee, trainer, "Morning Run",
                trainingType, LocalDate.of(2024, 4, 10), 60);
        when(trainingTypeMapper.toDTO(trainingType)).thenReturn(trainingTypeDTO);

        TrainingDTO dto = trainingMapper.toDTO(training);

        assertEquals("Morning Run", dto.getName());
    }

    @Test
    void testToDtoMapsDate() {
        LocalDate date = LocalDate.of(2024, 4, 10);
        Training training = buildTraining(5L, trainee, trainer, "Morning Run",
                trainingType, date, 60);
        when(trainingTypeMapper.toDTO(trainingType)).thenReturn(trainingTypeDTO);

        TrainingDTO dto = trainingMapper.toDTO(training);

        assertEquals(date, dto.getDate());
    }

    @Test
    void testToDtoMapsDuration() {
        Training training = buildTraining(5L, trainee, trainer, "Morning Run",
                trainingType, LocalDate.of(2024, 4, 10), 60);
        when(trainingTypeMapper.toDTO(trainingType)).thenReturn(trainingTypeDTO);

        TrainingDTO dto = trainingMapper.toDTO(training);

        assertEquals(60, dto.getDuration());
    }

    @Test
    void testToDtoDelegatesToTrainingTypeMapper() {
        Training training = buildTraining(5L, trainee, trainer, "Morning Run",
                trainingType, LocalDate.of(2024, 4, 10), 60);
        when(trainingTypeMapper.toDTO(trainingType)).thenReturn(trainingTypeDTO);

        trainingMapper.toDTO(training);

        verify(trainingTypeMapper).toDTO(trainingType);
    }

    @Test
    void testToDtoMapsTrainingTypeDtoFromMapper() {
        Training training = buildTraining(5L, trainee, trainer, "Morning Run",
                trainingType, LocalDate.of(2024, 4, 10), 60);
        when(trainingTypeMapper.toDTO(trainingType)).thenReturn(trainingTypeDTO);

        TrainingDTO dto = trainingMapper.toDTO(training);

        assertSame(trainingTypeDTO, dto.getTrainingTypeDto());
    }

    @Test
    void testToEntitySetsIdToNull() {
        TrainingDTO dto = buildTrainingDTO(5L, 10L, 20L, "Evening Yoga",
                trainingTypeDTO, LocalDate.of(2024, 5, 1), 45);
        when(trainingTypeMapper.toEntity(trainingTypeDTO)).thenReturn(trainingType);

        Training training = trainingMapper.toEntity(dto);

        assertNull(training.getId());
    }

    @Test
    void testToEntitySetsTraineeToNull() {
        TrainingDTO dto = buildTrainingDTO(5L, 10L, 20L, "Evening Yoga",
                trainingTypeDTO, LocalDate.of(2024, 5, 1), 45);
        when(trainingTypeMapper.toEntity(trainingTypeDTO)).thenReturn(trainingType);

        Training training = trainingMapper.toEntity(dto);

        assertNull(training.getTrainee());
    }

    @Test
    void testToEntitySetsTrainerToNull() {
        TrainingDTO dto = buildTrainingDTO(5L, 10L, 20L, "Evening Yoga",
                trainingTypeDTO, LocalDate.of(2024, 5, 1), 45);
        when(trainingTypeMapper.toEntity(trainingTypeDTO)).thenReturn(trainingType);

        Training training = trainingMapper.toEntity(dto);

        assertNull(training.getTrainer());
    }

    @Test
    void testToEntityMapsName() {
        TrainingDTO dto = buildTrainingDTO(5L, 10L, 20L, "Evening Yoga",
                trainingTypeDTO, LocalDate.of(2024, 5, 1), 45);
        when(trainingTypeMapper.toEntity(trainingTypeDTO)).thenReturn(trainingType);

        Training training = trainingMapper.toEntity(dto);

        assertEquals("Evening Yoga", training.getName());
    }

    @Test
    void testToEntityMapsDate() {
        LocalDate date = LocalDate.of(2024, 5, 1);
        TrainingDTO dto = buildTrainingDTO(5L, 10L, 20L, "Evening Yoga",
                trainingTypeDTO, date, 45);
        when(trainingTypeMapper.toEntity(trainingTypeDTO)).thenReturn(trainingType);

        Training training = trainingMapper.toEntity(dto);

        assertEquals(date, training.getDate());
    }

    @Test
    void testToEntityMapsDuration() {
        TrainingDTO dto = buildTrainingDTO(5L, 10L, 20L, "Evening Yoga",
                trainingTypeDTO, LocalDate.of(2024, 5, 1), 45);
        when(trainingTypeMapper.toEntity(trainingTypeDTO)).thenReturn(trainingType);

        Training training = trainingMapper.toEntity(dto);

        assertEquals(45, training.getDuration());
    }

    @Test
    void testToEntityDelegatesToTrainingTypeMapper() {
        TrainingDTO dto = buildTrainingDTO(5L, 10L, 20L, "Evening Yoga",
                trainingTypeDTO, LocalDate.of(2024, 5, 1), 45);
        when(trainingTypeMapper.toEntity(trainingTypeDTO)).thenReturn(trainingType);

        trainingMapper.toEntity(dto);

        verify(trainingTypeMapper).toEntity(trainingTypeDTO);
    }

    @Test
    void testToEntityMapsTrainingTypeFromMapper() {
        TrainingDTO dto = buildTrainingDTO(5L, 10L, 20L, "Evening Yoga",
                trainingTypeDTO, LocalDate.of(2024, 5, 1), 45);
        when(trainingTypeMapper.toEntity(trainingTypeDTO)).thenReturn(trainingType);

        Training training = trainingMapper.toEntity(dto);

        assertSame(trainingType, training.getTrainingType());
    }

    private Training buildTraining(Long id, Trainee trainee, Trainer trainer, String name,
                                   TrainingType trainingType, LocalDate date, int duration) {
        Training training = new Training();
        training.setId(id);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setName(name);
        training.setTrainingType(trainingType);
        training.setDate(date);
        training.setDuration(duration);
        return training;
    }

    private TrainingDTO buildTrainingDTO(Long pk, Long traineeId, Long trainerId, String name,
                                          TrainingTypeDTO typeDTO, LocalDate date, int duration) {
        TrainingDTO dto = new TrainingDTO();
        dto.setTrainingPk(pk);
        dto.setTraineeId(traineeId);
        dto.setTrainerId(trainerId);
        dto.setName(name);
        dto.setTrainingTypeDto(typeDTO);
        dto.setDate(date);
        dto.setDuration(duration);
        return dto;
    }
}
