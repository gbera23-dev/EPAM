import entities.Trainee;
import entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.TraineeServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeDAO traineeDAO;

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
        trainee = new Trainee(1L, null, "123 Main St", user);
    }

    @Test
    void testCreateTraineeProfilePasswordIsValid() {
        when(traineeDAO.getAll()).thenReturn(Collections.emptyList());

        traineeService.createTraineeProfile(trainee);

        assertNotNull(user.getPassword());
        assertFalse(user.getPassword().isEmpty());
    }

    @Test
    void testCreateTraineeProfileUsernameIsSerialized() {
        User existingUser = new User();
        existingUser.setUsername("John.Doe");
        Trainee existingTrainee = new Trainee(2L, null, "456 Other St", existingUser);

        when(traineeDAO.getAll()).thenReturn(List.of(existingTrainee));

        traineeService.createTraineeProfile(trainee);

        assertEquals("John.Doe1", user.getUsername());
    }

    @Test
    void testCreateTraineeProfileSavesTraineeToDAO() {
        when(traineeDAO.getAll()).thenReturn(Collections.emptyList());

        traineeService.createTraineeProfile(trainee);

        verify(traineeDAO).save(1L, trainee);
    }


    @Test
    void testSelectTraineeProfileReturnsTraineeWhenExists() {
        when(traineeDAO.getEntity(1L)).thenReturn(trainee);

        Trainee result = traineeService.selectTraineeProfile(1L);

        assertSame(trainee, result);
    }

    @Test
    void testSelectTraineeProfileReturnsNullWhenNotExists() {
        when(traineeDAO.getEntity(99L)).thenReturn(null);

        assertNull(traineeService.selectTraineeProfile(99L));
    }


    @Test
    void testUpdateTraineeProfileSavesUpdatedTrainee() {
        traineeService.updateTraineeProfile(trainee);

        verify(traineeDAO).save(1L, trainee);
    }

    @Test
    void testDeleteTraineeProfileDeletesTraineeFromDAO() {
        traineeService.deleteTraineeProfile(1L);

        verify(traineeDAO).delete(1L);
    }

    @Test
    void testDeleteTraineeProfileDoesNotInteractWithSave() {
        traineeService.deleteTraineeProfile(1L);

        verify(traineeDAO, never()).save(anyLong(), any());
    }
}