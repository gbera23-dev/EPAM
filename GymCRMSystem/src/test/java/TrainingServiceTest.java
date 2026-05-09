import entities.Training;
import entities.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import persistence.TrainingDAO;
import services.TrainingService;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingDAO trainingDAO;

    @InjectMocks
    private TrainingService trainingService;

    private Training createTraining(long pk) {
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setName("Strength");
        return new Training(pk, 10L, 20L, "Morning Training", type, new Date(), 60);
    }

    @Test
    void testSelectTrainingProfileReturnsTraining() {
        Training training = createTraining(1L);
        when(trainingDAO.getEntity(1L)).thenReturn(training);

        Training result = trainingService.selectTrainingProfile(1L);

        assertEquals(training, result);
    }

    @Test
    void testSelectTrainingProfileReturnsNullWhenNotFound() {
        when(trainingDAO.getEntity(99L)).thenReturn(null);

        Training result = trainingService.selectTrainingProfile(99L);

        assertNull(result);
    }

    @Test
    void testCreateTrainingProfileSavesTraining() {
        Training training = createTraining(2L);

        trainingService.createTrainingProfile(training);

        verify(trainingDAO).save(2L, training);
    }

    @Test
    void testCreateTrainingProfileCallsDAOWithCorrectKey() {
        Training training = createTraining(7L);

        trainingService.createTrainingProfile(training);

        verify(trainingDAO, times(1)).save(7L, training);
    }

    @Test
    void testSelectTrainingProfileCallsDAOWithCorrectId() {
        when(trainingDAO.getEntity(3L)).thenReturn(null);

        trainingService.selectTrainingProfile(3L);

        verify(trainingDAO).getEntity(3L);
    }
}
