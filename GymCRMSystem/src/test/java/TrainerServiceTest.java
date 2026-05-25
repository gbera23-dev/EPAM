import entities.Trainer;
import entities.Training;
import entities.TrainingType;
import entities.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import persistence.TrainerRepository;
import persistence.TrainingRepository;
import services.TrainerServiceImpl;

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

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer trainer;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        trainer = new Trainer();
        trainer.setUser(user);
        TrainingType trainingType = new TrainingType();
        trainingType.setName("yoga");
        trainer.setTrainingType(trainingType);
    }

    @Test
    void testCreateTrainerProfileGeneratesBaseUsername() {
        when(trainerRepository.getUsernameWithMaxNumberSuffix(trainer)).thenReturn(Collections.emptyList());

        trainerService.createTrainerProfile(trainer);

        assertEquals("John.Doe", user.getUsername());
    }

    @Test
    void testCreateTrainerProfileGeneratesNonEmptyPassword() {
        when(trainerRepository.getUsernameWithMaxNumberSuffix(trainer)).thenReturn(Collections.emptyList());

        trainerService.createTrainerProfile(trainer);

        assertNotNull(user.getPassword());
        assertFalse(user.getPassword().isEmpty());
    }

    @Test
    void testCreateTrainerProfileAppendsCounterWhenUsernameExists() {
        User existingUser = new User();
        existingUser.setUsername("John.Doe");
        when(trainerRepository.getUsernameWithMaxNumberSuffix(trainer)).thenReturn(List.of(existingUser));

        trainerService.createTrainerProfile(trainer);

        assertEquals("John.Doe1", user.getUsername());
    }

    @Test
    void testCreateTrainerProfileAppendsCorrectCountWhenMultipleExist() {
        User u1 = new User();
        u1.setUsername("John.Doe");
        User u2 = new User();
        u2.setUsername("John.Doe1");
        when(trainerRepository.getUsernameWithMaxNumberSuffix(trainer)).thenReturn(List.of(u1, u2));

        trainerService.createTrainerProfile(trainer);

        assertEquals("John.Doe2", user.getUsername());
    }

    @Test
    void testCreateTrainerProfileSavesTrainer() {
        when(trainerRepository.getUsernameWithMaxNumberSuffix(trainer)).thenReturn(Collections.emptyList());

        trainerService.createTrainerProfile(trainer);

        verify(trainerRepository).save(trainer);
    }

    @Test
    void testUpdateTrainerProfileSavesTrainer() {
        trainerService.updateTrainerProfile(trainer);

        verify(trainerRepository).save(trainer);
    }

    @Test
    void testUpdateTrainerProfileDoesNotDelete() {
        trainerService.updateTrainerProfile(trainer);

        verify(trainerRepository, never()).deleteById(any());
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