import entities.Training;
import entities.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import persistence.TrainingRepository;
import services.TrainingServiceImpl;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {



    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Training createTraining(long pk) {
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setName("Strength");
        return new Training(pk, null, null, "Morning Training", type, LocalDate.now(), 60);
    }

    @Test
    void testSelectTrainingProfileReturnsTraining() {
        Training training = createTraining(1L);
        when(trainingRepository.getReferenceById(1L)).thenReturn(training);

        Training result = trainingService.selectTrainingProfile(1L);

        assertEquals(training, result);
    }

    @Test
    void testSelectTrainingProfileReturnsNullWhenNotFound() {

        Training result = trainingService.selectTrainingProfile(99L);

        assertNull(result);
    }

    @Test
    void testCreateTrainingProfileSavesTraining() {
        Training training = createTraining(2L);

        trainingService.createTrainingProfile(training);

        verify(trainingRepository).save(training);
    }

    @Test
    void testCreateTrainingProfileCallsDAOWithCorrectKey() {
        Training training = createTraining(7L);

        trainingService.createTrainingProfile(training);

        verify(trainingRepository, times(1)).save(training);
    }

    @Test
    void testSelectTrainingProfileCallsDAOWithCorrectId() {
        when(trainingRepository.getReferenceById(3L)).thenReturn(null);

        trainingService.selectTrainingProfile(3L);

        verify(trainingRepository).getReferenceById(3L);
    }
}
