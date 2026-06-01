import dto.TrainerDTO;
import dto.UserDTO;
import entities.Trainer;
import entities.TrainingType;
import entities.User;
import mappers.TrainerMapper;
import mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerMapperTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private TrainerMapper trainerMapper;

    private User user;
    private UserDTO userDTO;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(2L);
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setUsername("Jane.Doe");
        user.setPassword("pass");
        user.setActive(true);

        userDTO = new UserDTO();
        userDTO.setUserId(2L);
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Doe");
        userDTO.setUsername("Jane.Doe");
        userDTO.setPassword("pass");
        userDTO.setActive(true);

        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setName("yoga");
    }

    @Test
    void testToDtoMapsTrainerPk() {
        Trainer trainer = buildTrainer(7L, trainingType, user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        TrainerDTO dto = trainerMapper.toDTO(trainer);

        assertEquals(7L, dto.getTrainerPk());
    }

    @Test
    void testToDtoMapsSpecializationFromTrainingTypeName() {
        Trainer trainer = buildTrainer(7L, trainingType, user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        TrainerDTO dto = trainerMapper.toDTO(trainer);

        assertEquals("yoga", dto.getSpecialization());
    }

    @Test
    void testToDtoDelegatesToUserMapper() {
        Trainer trainer = buildTrainer(7L, trainingType, user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        trainerMapper.toDTO(trainer);

        verify(userMapper).toDTO(user);
    }

    @Test
    void testToDtoMapsUserFromUserMapper() {
        Trainer trainer = buildTrainer(7L, trainingType, user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        TrainerDTO dto = trainerMapper.toDTO(trainer);

        assertSame(userDTO, dto.getUser());
    }

    @Test
    void testToEntitySetsIdToNull() {
        TrainerDTO dto = buildTrainerDTO(7L, "pilates", userDTO);
        when(userMapper.toEntity(userDTO)).thenReturn(user);

        Trainer trainer = trainerMapper.toEntity(dto);

        assertNull(trainer.getId());
    }

    @Test
    void testToEntityMapsSpecializationToTrainingTypeName() {
        TrainerDTO dto = buildTrainerDTO(7L, "pilates", userDTO);
        when(userMapper.toEntity(userDTO)).thenReturn(user);

        Trainer trainer = trainerMapper.toEntity(dto);

        assertEquals("pilates", trainer.getTrainingType().getName());
    }

    @Test
    void testToEntitySetsTrainingTypeIdToNull() {
        TrainerDTO dto = buildTrainerDTO(7L, "pilates", userDTO);
        when(userMapper.toEntity(userDTO)).thenReturn(user);

        Trainer trainer = trainerMapper.toEntity(dto);

        assertNull(trainer.getTrainingType().getId());
    }

    @Test
    void testToEntityDelegatesToUserMapper() {
        TrainerDTO dto = buildTrainerDTO(7L, "pilates", userDTO);
        when(userMapper.toEntity(userDTO)).thenReturn(user);

        trainerMapper.toEntity(dto);

        verify(userMapper).toEntity(userDTO);
    }

    @Test
    void testToEntityMapsUserFromUserMapper() {
        TrainerDTO dto = buildTrainerDTO(7L, "pilates", userDTO);
        when(userMapper.toEntity(userDTO)).thenReturn(user);

        Trainer trainer = trainerMapper.toEntity(dto);

        assertSame(user, trainer.getUser());
    }

    private Trainer buildTrainer(Long id, TrainingType trainingType, User user) {
        Trainer trainer = new Trainer();
        trainer.setId(id);
        trainer.setTrainingType(trainingType);
        trainer.setUser(user);
        return trainer;
    }

    private TrainerDTO buildTrainerDTO(Long pk, String specialization, UserDTO userDTO) {
        TrainerDTO dto = new TrainerDTO();
        dto.setTrainerPk(pk);
        dto.setSpecialization(specialization);
        dto.setUser(userDTO);
        return dto;
    }
}
