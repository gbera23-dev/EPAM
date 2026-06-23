import app.entities.Trainer;
import app.entities.Training;
import app.entities.TrainingType;
import app.entities.User;
import app.persistence.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import app.persistence.TrainerRepository;
import app.persistence.TrainingRepository;
import app.services.TrainerServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer trainer;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password");
        trainer = new Trainer();
        trainer.setUser(user);
        TrainingType trainingType = new TrainingType();
        trainingType.setName("yoga");
        trainer.setTrainingType(trainingType);
    }

    @Test
    void testCreateTrainerProfileGeneratesBaseUsername() {
        when(userRepository.findUsersByFirstNameAndLastName("John", "Doe")).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode(any(String.class))).thenReturn("password");

        trainerService.createTrainerProfile(trainer);

        assertEquals("John.Doe", user.getUsername());
    }

    @Test
    void testCreateTrainerProfileGeneratesNonEmptyPassword() {
        when(userRepository.findUsersByFirstNameAndLastName("John", "Doe")).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode(any(String.class))).thenReturn("password");

        trainerService.createTrainerProfile(trainer);

        assertNotNull(user.getPassword());
        assertFalse(user.getPassword().isEmpty());
    }

    @Test
    void testCreateTrainerProfileAppendsCounterWhenUsernameExists() {
        User existingUser = new User();
        existingUser.setUsername("John.Doe");
        when(userRepository.findUsersByFirstNameAndLastName("John", "Doe")).thenReturn(List.of(existingUser));
        when(passwordEncoder.encode(any(String.class))).thenReturn("password");

        trainerService.createTrainerProfile(trainer);

        assertEquals("John.Doe1", user.getUsername());
    }

    @Test
    void testCreateTrainerProfileAppendsCorrectCountWhenMultipleExist() {
        User u1 = new User();
        u1.setUsername("John.Doe");
        User u2 = new User();
        u2.setUsername("John.Doe1");
        when(userRepository.findUsersByFirstNameAndLastName("John", "Doe")).thenReturn(List.of(u1, u2));
        when(passwordEncoder.encode(any(String.class))).thenReturn("password");

        trainerService.createTrainerProfile(trainer);

        assertEquals("John.Doe2", user.getUsername());
    }

    @Test
    void testCreateTrainerProfileSavesTrainer() {
        when(userRepository.findUsersByFirstNameAndLastName("John", "Doe")).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode(any(String.class))).thenReturn("password");

        trainerService.createTrainerProfile(trainer);

        verify(trainerRepository).save(trainer);
    }

    @Test
    void testUpdateTrainerProfileSavesTrainer() {
        trainerService.updateTrainerProfile(trainer);

        verify(trainerRepository).save(trainer);
    }

    @Test
    void testSelectTrainerProfileByIdReturnsTrainer() {
        when(trainerRepository.getReferenceById(5L)).thenReturn(trainer);

        Trainer result = trainerService.selectTrainerProfileById(5L);

        assertSame(trainer, result);
    }

    @Test
    void testSelectTrainerProfileByUsernameReturnsTrainer() {
        when(trainerRepository.findByUserUsername("John.Doe")).thenReturn(trainer);

        Trainer result = trainerService.selectTrainerProfileByUsername("John.Doe");

        assertSame(trainer, result);
    }

    @Test
    void testSelectTrainerProfileByUsernameReturnsNullWhenNotFound() {
        when(trainerRepository.findByUserUsername("unknown")).thenReturn(null);

        assertNull(trainerService.selectTrainerProfileByUsername("unknown"));
    }

    @Test
    void testActivateTrainerProfileSetsActiveTrue() {
        user.setActive(false);
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        trainerService.activateTrainerProfile(1L);

        assertTrue(user.isActive());
    }

    @Test
    void testActivateTrainerProfileThrowsWhenNotFound() {
        when(trainerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> trainerService.activateTrainerProfile(99L));
    }

    @Test
    void testDeactivateTrainerProfileSetsActiveFalse() {
        user.setActive(true);
        when(trainerRepository.findById(1L)).thenReturn(Optional.of(trainer));

        trainerService.deactivateTrainerProfile(1L);

        assertFalse(user.isActive());
    }

    @Test
    void testDeactivateTrainerProfileThrowsWhenNotFound() {
        when(trainerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> trainerService.deactivateTrainerProfile(99L));
    }

    @Test
    void testGetTrainingsForTrainerDelegatesToRepository() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 6, 1);
        List<Training> expected = List.of(new Training());
        when(trainingRepository.findTrainingsByTrainerCriteria("John.Doe", from, to, "traineeName"))
                .thenReturn(expected);

        List<Training> result = trainerService.getTrainingsForTrainer("John.Doe", from, to, "traineeName");

        assertSame(expected, result);
    }
}