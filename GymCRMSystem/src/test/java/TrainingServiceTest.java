import app.entities.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import app.persistence.TraineeRepository;
import app.persistence.TrainerRepository;
import app.persistence.TrainingRepository;
import app.services.TrainingServiceImpl;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        User traineeUser = new User();
        traineeUser.setUsername("john.doe");
        trainee = new Trainee();
        trainee.setUser(traineeUser);

        trainingType = new TrainingType();
        trainingType.setName("yoga");

        User trainerUser = new User();
        trainerUser.setUsername("jane.doe");
        trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setTrainingType(trainingType);

        training = new Training();
    }

    @Test
    void testSelectTrainingReturnsTrainingWhenFound() {
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));

        Training result = trainingService.selectTraining(1L);

        assertSame(training, result);
    }

    @Test
    void testSelectTrainingThrowsWhenNotFound() {
        when(trainingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> trainingService.selectTraining(99L));
    }

    @Test
    void testAddTrainingSavesTraining() {
        LocalDate date = LocalDate.of(2024, 5, 20);
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(trainee);
        when(trainerRepository.findByUserUsername("jane.doe")).thenReturn(trainer);

        trainingService.addTraining("john.doe", "jane.doe", "Morning Session", date, 90);

        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void testAddTrainingSetsTrainingName() {
        LocalDate date = LocalDate.of(2024, 5, 20);
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(trainee);
        when(trainerRepository.findByUserUsername("jane.doe")).thenReturn(trainer);
        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);

        trainingService.addTraining("john.doe", "jane.doe", "Morning Session", date, 90);

        verify(trainingRepository).save(captor.capture());
        assertEquals("Morning Session", captor.getValue().getName());
    }

    @Test
    void testAddTrainingSetsTrainingDate() {
        LocalDate date = LocalDate.of(2024, 5, 20);
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(trainee);
        when(trainerRepository.findByUserUsername("jane.doe")).thenReturn(trainer);
        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);

        trainingService.addTraining("john.doe", "jane.doe", "Morning Session", date, 90);

        verify(trainingRepository).save(captor.capture());
        assertEquals(date, captor.getValue().getDate());
    }

    @Test
    void testAddTrainingSetsTrainingDuration() {
        LocalDate date = LocalDate.of(2024, 5, 20);
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(trainee);
        when(trainerRepository.findByUserUsername("jane.doe")).thenReturn(trainer);
        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);

        trainingService.addTraining("john.doe", "jane.doe", "Morning Session", date, 90);

        verify(trainingRepository).save(captor.capture());
        assertEquals(90, captor.getValue().getDuration());
    }

    @Test
    void testAddTrainingSetsTrainingTypeFromTrainer() {
        LocalDate date = LocalDate.of(2024, 5, 20);
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(trainee);
        when(trainerRepository.findByUserUsername("jane.doe")).thenReturn(trainer);
        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);

        trainingService.addTraining("john.doe", "jane.doe", "Morning Session", date, 90);

        verify(trainingRepository).save(captor.capture());
        assertEquals("yoga", captor.getValue().getTrainingType().getName());
    }

    @Test
    void testAddTrainingAssociatesCorrectTrainee() {
        LocalDate date = LocalDate.of(2024, 5, 20);
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(trainee);
        when(trainerRepository.findByUserUsername("jane.doe")).thenReturn(trainer);
        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);

        trainingService.addTraining("john.doe", "jane.doe", "Morning Session", date, 90);

        verify(trainingRepository).save(captor.capture());
        assertSame(trainee, captor.getValue().getTrainee());
    }

    @Test
    void testAddTrainingAssociatesCorrectTrainer() {
        LocalDate date = LocalDate.of(2024, 5, 20);
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(trainee);
        when(trainerRepository.findByUserUsername("jane.doe")).thenReturn(trainer);
        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);

        trainingService.addTraining("john.doe", "jane.doe", "Morning Session", date, 90);

        verify(trainingRepository).save(captor.capture());
        assertSame(trainer, captor.getValue().getTrainer());
    }

    @Test
    void testAddTrainingThrowsWhenTraineeNotFound() {
        LocalDate date = LocalDate.of(2024, 5, 20);
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(null);
        when(trainerRepository.findByUserUsername("jane.doe")).thenReturn(trainer);

        assertThrows(EntityNotFoundException.class,
                () -> trainingService.addTraining("unknown", "jane.doe", "Session", date, 60));
    }

    @Test
    void testAddTrainingThrowsWhenTrainerNotFound() {
        LocalDate date = LocalDate.of(2024, 5, 20);
        when(traineeRepository.findByUserUsername("john.doe")).thenReturn(trainee);
        when(trainerRepository.findByUserUsername("unknown")).thenReturn(null);

        assertThrows(EntityNotFoundException.class,
                () -> trainingService.addTraining("john.doe", "unknown", "Session", date, 60));
    }

    @Test
    void testAddTrainingDoesNotSaveWhenTraineeNotFound() {
        LocalDate date = LocalDate.of(2024, 5, 20);
        when(traineeRepository.findByUserUsername("unknown")).thenReturn(null);
        when(trainerRepository.findByUserUsername("jane.doe")).thenReturn(trainer);

        assertThrows(EntityNotFoundException.class,
                () -> trainingService.addTraining("unknown", "jane.doe", "Session", date, 60));

        verify(trainingRepository, never()).save(any());
    }
}