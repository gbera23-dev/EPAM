import dto.TraineeDTO;
import dto.UserDTO;
import entities.Trainee;
import entities.User;
import mappers.TraineeMapper;
import mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeMapperTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private TraineeMapper traineeMapper;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setPassword("pass");
        user.setActive(true);

        userDTO = new UserDTO();
        userDTO.setUserId(1L);
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setUsername("John.Doe");
        userDTO.setPassword("pass");
        userDTO.setActive(true);
    }

    @Test
    void testToDtoMapsId() {
        Trainee trainee = buildTrainee(5L, LocalDate.of(1995, 3, 12), "123 Main St", user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        TraineeDTO dto = traineeMapper.toDTO(trainee);

        assertEquals(5L, dto.getTraineePk());
    }

    @Test
    void testToDtoMapsDateOfBirth() {
        LocalDate dob = LocalDate.of(1995, 3, 12);
        Trainee trainee = buildTrainee(5L, dob, "123 Main St", user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        TraineeDTO dto = traineeMapper.toDTO(trainee);

        assertEquals(dob, dto.getDateOfBirth());
    }

    @Test
    void testToDtoMapsAddress() {
        Trainee trainee = buildTrainee(5L, LocalDate.of(1995, 3, 12), "123 Main St", user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        TraineeDTO dto = traineeMapper.toDTO(trainee);

        assertEquals("123 Main St", dto.getAddress());
    }

    @Test
    void testToDtoDelegatesToUserMapper() {
        Trainee trainee = buildTrainee(5L, LocalDate.of(1995, 3, 12), "123 Main St", user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        traineeMapper.toDTO(trainee);

        verify(userMapper).toDTO(user);
    }

    @Test
    void testToDtoMapsUserFromUserMapper() {
        Trainee trainee = buildTrainee(5L, LocalDate.of(1995, 3, 12), "123 Main St", user);
        when(userMapper.toDTO(user)).thenReturn(userDTO);

        TraineeDTO dto = traineeMapper.toDTO(trainee);

        assertSame(userDTO, dto.getUser());
    }

    @Test
    void testToEntitySetsIdToNull() {
        TraineeDTO dto = buildTraineeDTO(5L, LocalDate.of(1990, 7, 20), "456 Other St", userDTO);
        when(userMapper.toEntity(userDTO)).thenReturn(user);

        Trainee trainee = traineeMapper.toEntity(dto);

        assertNull(trainee.getId());
    }

    @Test
    void testToEntityMapsDateOfBirth() {
        LocalDate dob = LocalDate.of(1990, 7, 20);
        TraineeDTO dto = buildTraineeDTO(5L, dob, "456 Other St", userDTO);
        when(userMapper.toEntity(userDTO)).thenReturn(user);

        Trainee trainee = traineeMapper.toEntity(dto);

        assertEquals(dob, trainee.getDateOfBirth());
    }

    @Test
    void testToEntityMapsAddress() {
        TraineeDTO dto = buildTraineeDTO(5L, LocalDate.of(1990, 7, 20), "456 Other St", userDTO);
        when(userMapper.toEntity(userDTO)).thenReturn(user);

        Trainee trainee = traineeMapper.toEntity(dto);

        assertEquals("456 Other St", trainee.getAddress());
    }

    @Test
    void testToEntityDelegatesToUserMapper() {
        TraineeDTO dto = buildTraineeDTO(5L, LocalDate.of(1990, 7, 20), "456 Other St", userDTO);
        when(userMapper.toEntity(userDTO)).thenReturn(user);

        traineeMapper.toEntity(dto);

        verify(userMapper).toEntity(userDTO);
    }

    @Test
    void testToEntityMapsUserFromUserMapper() {
        TraineeDTO dto = buildTraineeDTO(5L, LocalDate.of(1990, 7, 20), "456 Other St", userDTO);
        when(userMapper.toEntity(userDTO)).thenReturn(user);

        Trainee trainee = traineeMapper.toEntity(dto);

        assertSame(user, trainee.getUser());
    }

    private Trainee buildTrainee(Long id, LocalDate dob, String address, User user) {
        Trainee trainee = new Trainee();
        trainee.setId(id);
        trainee.setDateOfBirth(dob);
        trainee.setAddress(address);
        trainee.setUser(user);
        return trainee;
    }

    private TraineeDTO buildTraineeDTO(Long id, LocalDate dob, String address, UserDTO userDTO) {
        return new TraineeDTO(id, dob, address, userDTO);
    }
}
