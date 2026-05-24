import entities.Trainee;
import entities.Trainer;
import entities.Training;
import facade.GymFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import services.TraineeService;
import services.TrainerService;
import services.TrainingService;

import java.time.LocalDate;
import java.util.List;

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

    private Trainee trainee;
    private Trainer trainer;
    private Training training;

    @BeforeEach
    void setUp() {
        trainee = new Trainee();
        trainer = new Trainer();
        training = new Training();
    }

    @Test
    void testCreateTraineeDelegatesToTraineeService() {
        gymFacade.createTrainee(trainee);

        verify(traineeService).createTraineeProfile(trainee);
    }

    @Test
    void testUpdateTraineeDelegatesToTraineeService() {
        gymFacade.updateTrainee(trainee);

        verify(traineeService).updateTraineeProfile(trainee);
    }

    @Test
    void testDeleteTraineeByIdDelegatesToTraineeService() {
        gymFacade.deleteTraineeById(1L);

        verify(traineeService).deleteTraineeProfileById(1L);
    }

    @Test
    void testDeleteTraineeByUsernameDelegatesToTraineeService() {
        gymFacade.deleteTraineeByUsername("john.doe");

        verify(traineeService).deleteTraineeProfileByUsername("john.doe");
    }

    @Test
    void testGetTraineeByIdReturnsTraineeFromService() {
        when(traineeService.selectTraineeProfileById(1L)).thenReturn(trainee);

        Trainee result = gymFacade.getTraineeById(1L);

        assertSame(trainee, result);
    }

    @Test
    void testGetTraineeByUsernameReturnsTraineeFromService() {
        when(traineeService.selectTraineeProfileByUsername("john.doe")).thenReturn(trainee);

        Trainee result = gymFacade.getTraineeByUsername("john.doe");

        assertSame(trainee, result);
    }

    @Test
    void testChangeTraineePasswordDelegatesToTraineeService() {
        gymFacade.changeTraineePassword("john.doe", "oldPass", "newPass");

        verify(traineeService).changeTraineeProfilePassword("john.doe", "oldPass", "newPass");
    }

    @Test
    void testActivateTraineeDelegatesToTraineeService() {
        gymFacade.activateTrainee(1L);

        verify(traineeService).activateTraineeProfile(1L);
    }

    @Test
    void testDeactivateTraineeDelegatesToTraineeService() {
        gymFacade.deactivateTrainee(1L);

        verify(traineeService).deactivateTraineeProfile(1L);
    }

    @Test
    void testGetTrainingsForTraineeDelegatesToTraineeService() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 6, 1);
        List<Training> expected = List.of(training);
        when(traineeService.getTrainingsForTrainee("john.doe", from, to, "trainer", "yoga"))
                .thenReturn(expected);

        List<Training> result = gymFacade.getTrainingsForTrainee("john.doe", from, to, "trainer", "yoga");

        assertSame(expected, result);
    }

    @Test
    void testGetTrainersNotAssignedToTraineeDelegatesToTraineeService() {
        List<Trainer> expected = List.of(trainer);
        when(traineeService.getTrainersNotAssignedToTrainee("john.doe")).thenReturn(expected);

        List<Trainer> result = gymFacade.getTrainersNotAssignedToTrainee("john.doe");

        assertSame(expected, result);
    }

    @Test
    void testUpdateTraineeListOfTrainersDelegatesToTraineeService() {
        List<String> usernames = List.of("trainer1", "trainer2");

        gymFacade.updateTraineeListOfTrainers(1L, usernames);

        verify(traineeService).updateTraineeListOfTrainers(1L, usernames);
    }

    @Test
    void testValidateTraineeReturnsTrueFromService() {
        when(traineeService.validateTraineeProfile("john.doe", "pass")).thenReturn(true);

        assertTrue(gymFacade.validateTrainee("john.doe", "pass"));
    }

    @Test
    void testValidateTraineeReturnsFalseFromService() {
        when(traineeService.validateTraineeProfile("john.doe", "wrongPass")).thenReturn(false);

        assertFalse(gymFacade.validateTrainee("john.doe", "wrongPass"));
    }

    @Test
    void testCreateTrainerDelegatesToTrainerService() {
        gymFacade.createTrainer(trainer);

        verify(trainerService).createTrainerProfile(trainer);
    }

    @Test
    void testUpdateTrainerDelegatesToTrainerService() {
        gymFacade.updateTrainer(trainer);

        verify(trainerService).updateTrainerProfile(trainer);
    }

    @Test
    void testGetTrainerByIdReturnsTrainerFromService() {
        when(trainerService.selectTrainerProfileById(2L)).thenReturn(trainer);

        Trainer result = gymFacade.getTrainerById(2L);

        assertSame(trainer, result);
    }

    @Test
    void testGetTrainerByUsernameReturnsTrainerFromService() {
        when(trainerService.selectTrainerProfileByUsername("jane.doe")).thenReturn(trainer);

        Trainer result = gymFacade.getTrainerByUsername("jane.doe");

        assertSame(trainer, result);
    }

    @Test
    void testChangeTrainerPasswordDelegatesToTrainerService() {
        gymFacade.changeTrainerPassword("jane.doe", "oldPass", "newPass");

        verify(trainerService).changeTrainerProfilePassword("jane.doe", "oldPass", "newPass");
    }

    @Test
    void testActivateTrainerDelegatesToTrainerService() {
        gymFacade.activateTrainer(2L);

        verify(trainerService).activateTrainerProfile(2L);
    }

    @Test
    void testDeactivateTrainerDelegatesToTrainerService() {
        gymFacade.deactivateTrainer(2L);

        verify(trainerService).deactivateTrainerProfile(2L);
    }

    @Test
    void testGetTrainingsForTrainerDelegatesToTrainerService() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 6, 1);
        List<Training> expected = List.of(training);
        when(trainerService.getTrainingsForTrainer("jane.doe", from, to, "trainee")).thenReturn(expected);

        List<Training> result = gymFacade.getTrainingsForTrainer("jane.doe", from, to, "trainee");

        assertSame(expected, result);
    }

    @Test
    void testValidateTrainerReturnsTrueFromService() {
        when(trainerService.validateTrainerProfile("jane.doe", "pass")).thenReturn(true);

        assertTrue(gymFacade.validateTrainer("jane.doe", "pass"));
    }

    @Test
    void testValidateTrainerReturnsFalseFromService() {
        when(trainerService.validateTrainerProfile("jane.doe", "wrongPass")).thenReturn(false);

        assertFalse(gymFacade.validateTrainer("jane.doe", "wrongPass"));
    }

    @Test
    void testGetTrainingReturnsTrainingFromService() {
        when(trainingService.selectTraining(3L)).thenReturn(training);

        Training result = gymFacade.getTraining(3L);

        assertSame(training, result);
    }

    @Test
    void testAddTrainingDelegatesToTrainingService() {
        LocalDate date = LocalDate.of(2024, 3, 15);

        gymFacade.addTraining("john.doe", "jane.doe", "Session 1", date, 60);

        verify(trainingService).addTraining("john.doe", "jane.doe", "Session 1", date, 60);
    }
}