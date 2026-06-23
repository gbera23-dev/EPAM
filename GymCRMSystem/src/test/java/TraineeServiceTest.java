import app.entities.Trainee;
import app.entities.Trainer;
import app.entities.Training;
import app.entities.User;
import app.exceptions.UserNotFoundException;
import app.persistence.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import app.persistence.TraineeRepository;
import app.persistence.TrainerRepository;
import app.persistence.TrainingRepository;
import app.services.TraineeServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private Trainee trainee;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        trainee = new Trainee();
        trainee.setTrainers(new ArrayList<>());
        trainee.setUser(user);
    }

    @Test
    void testCreateTraineeProfileGeneratesNonEmptyPassword() {
        when(userRepository.findUsersByFirstNameAndLastName("John", "Doe")).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode(any(String.class))).thenReturn("password");

        traineeService.createTraineeProfile(trainee);

        assertNotNull(user.getPassword());
        assertFalse(user.getPassword().isEmpty());
    }

    @Test
    void testCreateTraineeProfileGeneratesBaseUsername() {
        when(userRepository.findUsersByFirstNameAndLastName("John", "Doe")).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode(any(String.class))).thenReturn("password");

        traineeService.createTraineeProfile(trainee);

        assertEquals("John.Doe", user.getUsername());
    }

    @Test
    void testCreateTraineeProfileAppendsCounterWhenUsernameExists() {
        User existingUser = new User();
        existingUser.setUsername("John.Doe");
        when(userRepository.findUsersByFirstNameAndLastName("John", "Doe")).thenReturn(List.of(existingUser));
        when(passwordEncoder.encode(any(String.class))).thenReturn("password");

        traineeService.createTraineeProfile(trainee);

        assertEquals("John.Doe1", user.getUsername());
    }

    @Test
    void testCreateTraineeProfileAppendsCorrectCountWhenMultipleExist() {
        User u1 = new User();
        u1.setUsername("John.Doe");
        User u2 = new User();
        u2.setUsername("John.Doe1");
        when(userRepository.findUsersByFirstNameAndLastName("John", "Doe")).thenReturn(List.of(u1, u2));
        when(passwordEncoder.encode(any(String.class))).thenReturn("password");

        traineeService.createTraineeProfile(trainee);

        assertEquals("John.Doe2", user.getUsername());
    }

    @Test
    void testCreateTraineeProfileSavesTrainee() {
        when(userRepository.findUsersByFirstNameAndLastName("John", "Doe")).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode(any(String.class))).thenReturn("password");

        traineeService.createTraineeProfile(trainee);

        verify(traineeRepository).save(trainee);
    }

    @Test
    void testSelectTraineeProfileByIdReturnsTrainee() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.selectTraineeProfileById(1L);

        assertSame(trainee, result);
    }

    @Test
    void testSelectTraineeProfileByUsernameReturnsTrainee() {
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(trainee);

        Trainee result = traineeService.selectTraineeProfileByUsername("John.Doe");

        assertSame(trainee, result);
    }

    @Test
    void testSelectTraineeProfileByUsernameReturnsNullWhenNotFound() {
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(null);

        assertNull(traineeService.selectTraineeProfileByUsername("unknown"));
    }

    @Test
    void testUpdateTraineeProfileSavesTrainee() {
        traineeService.updateTraineeProfile(trainee);

        verify(traineeRepository).save(trainee);
    }

    @Test
    void testUpdateTraineeProfileDoesNotDelete() {
        traineeService.updateTraineeProfile(trainee);

        verify(traineeRepository, never()).deleteById(any());
    }

    @Test
    void testUpdateTraineeListOfTrainersClearsAndSetsNewTrainers() {
        List<Trainer> newTrainers = List.of(new Trainer(), new Trainer());

        newTrainers.get(0).setTrainees(new ArrayList<>());
        newTrainers.get(1).setTrainees(new ArrayList<>());

        List<String> usernames = List.of("trainer1", "trainer2");
        trainee.setTrainers(new java.util.ArrayList<>());
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));

        trainee.getTrainers().clear();

        when(trainerRepository.findByUserUsernameIn(usernames)).thenReturn(newTrainers);

        traineeService.updateTraineeListOfTrainers(1L, usernames);

        assertEquals(2, trainee.getTrainers().size());
    }

    @Test
    void testUpdateTraineeListOfTrainersThrowsWhenTraineeNotFound() {
        when(traineeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> traineeService.updateTraineeListOfTrainers(99L, List.of("trainer1")));
    }

    @Test
    void testDeleteTraineeProfileByIdDeletesTrainee() {
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));

        traineeService.deleteTraineeProfileById(1L);

        verify(traineeRepository).delete(trainee);
    }

    @Test
    void testDeleteTraineeProfileByUsernameDeletesTrainee() {
        String name = "John.Doe";

        when(traineeRepository.findByUserUsername(name)).thenReturn(trainee);

        traineeService.deleteTraineeProfileByUsername(name);

        verify(traineeRepository).findByUserUsername(name);
        verify(traineeRepository).delete(trainee);
    }

    @Test
    void testActivateTraineeProfileSetsActiveTrue() {
        user.setActive(false);
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));

        traineeService.activateTraineeProfile(1L);

        assertTrue(user.isActive());
    }

    @Test
    void testActivateTraineeProfileThrowsWhenNotFound() {
        when(traineeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> traineeService.activateTraineeProfile(99L));
    }

    @Test
    void testDeactivateTraineeProfileSetsActiveFalse() {
        user.setActive(true);
        when(traineeRepository.findById(1L)).thenReturn(Optional.of(trainee));

        traineeService.deactivateTraineeProfile(1L);

        assertFalse(user.isActive());
    }

    @Test
    void testDeactivateTraineeProfileThrowsWhenNotFound() {
        when(traineeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> traineeService.deactivateTraineeProfile(99L));
    }

    @Test
    void testGetTrainingsForTraineeDelegatesToRepository() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 6, 1);
        List<Training> expected = List.of(new Training());
        when(trainingRepository.findTrainingsByTraineeCriteria("John.Doe", from, to, "trainerName", "yoga"))
                .thenReturn(expected);

        List<Training> result = traineeService.getTrainingsForTrainee("John.Doe", from, to, "trainerName", "yoga");

        assertSame(expected, result);
    }

    @Test
    void testGetTrainersNotAssignedToTraineeDelegatesToRepository() {
        List<Trainer> expected = List.of(new Trainer());
        when(trainerRepository.findTrainersNotAssignedToTrainee("John.Doe")).thenReturn(expected);

        List<Trainer> result = traineeService.getTrainersNotAssignedToTrainee("John.Doe");

        assertSame(expected, result);
    }
}