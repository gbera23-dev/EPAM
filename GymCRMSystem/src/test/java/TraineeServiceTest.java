import entities.Trainee;
import entities.User;
import mappers.TraineeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import persistence.TraineeRepository;
import services.TraineeServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee trainee;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("");
        user.setPassword("");
        trainee = new Trainee(1L, null, "123 Main St", user, null, null);
    }

    @Test
    void testCreateTraineeProfilePasswordIsValid() {
        when(traineeRepository.findAll()).thenReturn(Collections.emptyList());

        traineeService.createTraineeProfile(trainee);

        assertNotNull(user.getPassword());
        assertFalse(user.getPassword().isEmpty());
    }

    @Test
    void testCreateTraineeProfileUsernameIsSerialized() {
        User existingUser = new User();
        existingUser.setUsername("John.Doe");
        Trainee existingTrainee = new Trainee(2L, null, "456 Other St", existingUser, null, null);

        when(traineeRepository.findAll()).thenReturn(List.of(existingTrainee));

        traineeService.createTraineeProfile(trainee);

        assertEquals("John.Doe1", user.getUsername());
    }

    @Test
    void testCreateTraineeProfileSavesTraineeToDAO() {
        when(traineeRepository.findAll()).thenReturn(Collections.emptyList());

        traineeService.createTraineeProfile(trainee);

        verify(traineeRepository).save(trainee);
    }


    @Test
    void testSelectTraineeProfileReturnsTraineeWhenExists() {
        when(traineeRepository.getReferenceById(1L)).thenReturn(trainee);

        Trainee result = traineeService.selectTraineeProfile(1L);

        assertSame(trainee, result);
    }

    @Test
    void testSelectTraineeProfileReturnsNullWhenNotExists() {
        when(traineeRepository.getReferenceById(99L)).thenReturn(null);

        assertNull(traineeService.selectTraineeProfile(99L));
    }


    @Test
    void testUpdateTraineeProfileSavesUpdatedTrainee() {
        traineeService.updateTraineeProfile(trainee);

        verify(traineeRepository).save(trainee);
    }

    @Test
    void testDeleteTraineeProfileDeletesTraineeFromDAO() {
        traineeService.deleteTraineeProfile(1L);

        verify(traineeRepository).deleteById(1L);
    }

    @Test
    void testDeleteTraineeProfileDoesNotInteractWithSave() {
        traineeService.deleteTraineeProfile(1L);

        verify(traineeRepository, never()).save(any());
    }
}