import entities.Trainee;
import entities.Trainer;
import entities.Training;
import entities.TrainingType;
import entities.User;
import facade.GymFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.TraineeService;
import services.TrainerService;
import services.TrainingService;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymFacade gymFacade;

    private Trainee createTrainee(long pk) {
        return new Trainee(pk, null, "Address", new User(pk, "F", "L", "f.l", "pw", true));
    }

    private Trainer createTrainer(long pk) {
        return new Trainer(pk, "Yoga", new User(pk, "T", "R", "t.r", "pw", true));
    }

    private Training createTraining(long pk) {
        return new Training(pk, 1L, 2L, "Session", new TrainingType(), new Date(), 30);
    }

    @Test
    void testCreateTraineeDelegatesToTraineeService() {
        Trainee trainee = createTrainee(1L);

        gymFacade.createTrainee(trainee);

        verify(traineeService).createTraineeProfile(trainee);
    }

    @Test
    void testUpdateTraineeDelegatesToTraineeService() {
        Trainee trainee = createTrainee(2L);

        gymFacade.updateTrainee(trainee);

        verify(traineeService).updateTraineeProfile(trainee);
    }

    @Test
    void testDeleteTraineeDelegatesToTraineeService() {
        gymFacade.deleteTrainee(3L);

        verify(traineeService).deleteTraineeProfile(3L);
    }

    @Test
    void testGetTraineeReturnsCorrectTrainee() {
        Trainee trainee = createTrainee(4L);
        when(traineeService.selectTraineeProfile(4L)).thenReturn(trainee);

        Trainee result = gymFacade.getTrainee(4L);

        assertEquals(trainee, result);
    }

    @Test
    void testGetTraineeReturnsNullWhenNotFound() {
        when(traineeService.selectTraineeProfile(99L)).thenReturn(null);

        Trainee result = gymFacade.getTrainee(99L);

        assertNull(result);
    }

    @Test
    void testCreateTrainerDelegatesToTrainerService() {
        Trainer trainer = createTrainer(1L);

        gymFacade.createTrainer(trainer);

        verify(trainerService).createTrainerProfile(trainer);
    }

    @Test
    void testUpdateTrainerDelegatesToTrainerService() {
        Trainer trainer = createTrainer(2L);

        gymFacade.updateTrainer(trainer);

        verify(trainerService).updateTrainerProfile(trainer);
    }

    @Test
    void testGetTrainerReturnsCorrectTrainer() {
        Trainer trainer = createTrainer(5L);
        when(trainerService.selectTrainerProfile(5L)).thenReturn(trainer);

        Trainer result = gymFacade.getTrainer(5L);

        assertEquals(trainer, result);
    }

    @Test
    void testGetTrainerReturnsNullWhenNotFound() {
        when(trainerService.selectTrainerProfile(88L)).thenReturn(null);

        Trainer result = gymFacade.getTrainer(88L);

        assertNull(result);
    }

    @Test
    void testAddTrainingDelegatesToTrainingService() {
        Training training = createTraining(1L);

        gymFacade.addTraining(training);

        verify(trainingService).createTrainingProfile(training);
    }

    @Test
    void testGetTrainingReturnsCorrectTraining() {
        Training training = createTraining(6L);
        when(trainingService.selectTrainingProfile(6L)).thenReturn(training);

        Training result = gymFacade.getTraining(6L);

        assertEquals(training, result);
    }

    @Test
    void testGetTrainingReturnsNullWhenNotFound() {
        when(trainingService.selectTrainingProfile(77L)).thenReturn(null);

        Training result = gymFacade.getTraining(77L);

        assertNull(result);
    }
}
