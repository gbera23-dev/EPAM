import entities.Trainer;
import entities.TrainingType;
import entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import persistence.TrainerRepository;
import services.TrainerServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private Trainer createTrainer(long pk, String firstName, String lastName, String username) {
        User user = new User(pk, firstName, lastName, username, "pass", true, null, null);
        TrainingType trainingType = new TrainingType();
        trainingType.setName("yoga");
        return new Trainer(pk, trainingType, user, null, null);
    }

    @Test
    void testCreateTrainerProfileSavesTrainer() {
        Trainer trainer = createTrainer(1L, "John", "Doe", null);
        when(trainerRepository.findAll()).thenReturn(Collections.emptyList());

        trainerService.createTrainerProfile(trainer);

        verify(trainerRepository).save(trainer);
    }

    @Test
    void testCreateTrainerProfileSetsGeneratedUsername() {
        Trainer trainer = createTrainer(1L, "John", "Doe", null);
        when(trainerRepository.findAll()).thenReturn(Collections.emptyList());

        trainerService.createTrainerProfile(trainer);

        assertEquals("John.Doe", trainer.getUser().getUsername());
    }

    @Test
    void testCreateTrainerProfileSetsPassword() {
        Trainer trainer = createTrainer(1L, "Alice", "Smith", null);
        when(trainerRepository.findAll()).thenReturn(Collections.emptyList());

        trainerService.createTrainerProfile(trainer);

        assertNotNull(trainer.getUser().getPassword());
        assertFalse(trainer.getUser().getPassword().isEmpty());
    }

    @Test
    void testCreateTrainerProfileAppendsCounterWhenUsernameExists() {
        Trainer existing = createTrainer(2L, "Jane", "Doe", "Jane.Doe");
        when(trainerRepository.findAll()).thenReturn(List.of(existing));

        Trainer newTrainer = createTrainer(3L, "Jane", "Doe", null);
        trainerService.createTrainerProfile(newTrainer);

        assertEquals("Jane.Doe1", newTrainer.getUser().getUsername());
    }

    @Test
    void testCreateTrainerProfileAppendsCorrectCountWhenMultipleExist() {
        Trainer t1 = createTrainer(1L, "Sam", "Lee", "Sam.Lee");
        Trainer t2 = createTrainer(2L, "Sam", "Lee", "Sam.Lee1");
        when(trainerRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        Trainer newTrainer = createTrainer(3L, "Sam", "Lee", null);
        trainerService.createTrainerProfile(newTrainer);

        assertEquals("Sam.Lee2", newTrainer.getUser().getUsername());
    }

    @Test
    void testUpdateTrainerProfileSavesTrainer() {
        Trainer trainer = createTrainer(10L, "Bob", "Brown", "bob.brown");

        trainerService.updateTrainerProfile(trainer);

        verify(trainerRepository).save(trainer);
    }

    @Test
    void testSelectTrainerProfileReturnsTrainer() {
        Trainer trainer = createTrainer(5L, "Eve", "White", "eve.white");
        when(trainerRepository.getReferenceById(5L)).thenReturn(trainer);

        Trainer result = trainerService.selectTrainerProfile(5L);

        assertEquals(trainer, result);
    }

    @Test
    void testSelectTrainerProfileReturnsNullWhenNotFound() {

        Trainer result = trainerService.selectTrainerProfile(99L);

        assertNull(result);
    }
}
