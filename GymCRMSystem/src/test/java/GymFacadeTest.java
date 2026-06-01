import auth.SecurityContextHolder;
import dto.TraineeDTO;
import dto.TrainerDTO;
import dto.TrainingDTO;
import entities.Trainee;
import entities.Trainer;
import entities.Training;
import facade.GymFacade;
import mappers.GymMapper;
import mappers.TraineeMapper;
import mappers.TrainerMapper;
import mappers.TrainingMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import services.AuthService;
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

    @Mock
    private AuthService authService;

    @Mock
    private GymMapper mapper;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private GymFacade gymFacade;

    private TraineeDTO traineeDTO;
    private TrainerDTO trainerDTO;
    private TrainingDTO trainingDTO;
    private Trainee traineeEntity;
    private Trainer trainerEntity;
    private Training trainingEntity;

    private MockedStatic<SecurityContextHolder> securityContextHolder;

    @BeforeEach
    void setUp() {
        traineeDTO = new TraineeDTO();
        trainerDTO = new TrainerDTO();
        trainingDTO = new TrainingDTO();
        traineeEntity = new Trainee();
        trainerEntity = new Trainer();
        trainingEntity = new Training();

        lenient().when(mapper.getTraineeMapper()).thenReturn(traineeMapper);
        lenient().when(mapper.getTrainerMapper()).thenReturn(trainerMapper);
        lenient().when(mapper.getTrainingMapper()).thenReturn(trainingMapper);

        securityContextHolder = mockStatic(SecurityContextHolder.class);
    }

    @AfterEach
    void tearDown() {
        securityContextHolder.close();
    }

    @Test
    void testLoginUserSuccess() {
        securityContextHolder.when(SecurityContextHolder::getCurrentUser).thenReturn(null);
        when(authService.validateUserProfile("john.doe", "pass")).thenReturn(true);

        gymFacade.loginUser("john.doe", "pass");

        securityContextHolder.verify(() -> SecurityContextHolder.setCurrentUser("john.doe"));
        verify(authService).loginUserProfile("john.doe", "pass");
    }

    @Test
    void testLoginUserAlreadyLoggedIn() {
        securityContextHolder.when(SecurityContextHolder::getCurrentUser).thenReturn("john.doe");

        assertThrows(IllegalStateException.class, () -> gymFacade.loginUser("john.doe", "pass"));

        verify(authService, never()).validateUserProfile(any(), any());
    }

    @Test
    void testLoginUserInvalidCredentials() {
        securityContextHolder.when(SecurityContextHolder::getCurrentUser).thenReturn(null);
        when(authService.validateUserProfile("john.doe", "wrong")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> gymFacade.loginUser("john.doe", "wrong"));

        verify(authService, never()).loginUserProfile(any(), any());
    }

    @Test
    void testLogoutUserSuccess() {
        securityContextHolder.when(SecurityContextHolder::getCurrentUser).thenReturn("john.doe");

        gymFacade.logoutUser();

        verify(authService).logoutUserProfile("john.doe");
        securityContextHolder.verify(SecurityContextHolder::clear);
    }

    @Test
    void testLogoutUserNotLoggedIn() {
        securityContextHolder.when(SecurityContextHolder::getCurrentUser).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> gymFacade.logoutUser());

        verify(authService, never()).logoutUserProfile(any());
    }

    @Test
    void testChangeUserPasswordSuccess() {
        securityContextHolder.when(SecurityContextHolder::getCurrentUser).thenReturn("john.doe");

        gymFacade.changeUserPassword("newPass123");

        verify(authService).changeUserProfilePassword("john.doe", "newPass123");
    }

    @Test
    void testChangeUserPasswordNotLoggedIn() {
        securityContextHolder.when(SecurityContextHolder::getCurrentUser).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> gymFacade.changeUserPassword("newPass123"));

        verify(authService, never()).changeUserProfilePassword(any(), any());
    }

    @Test
    void testCreateTraineeDelegatesToTraineeService() {
        when(traineeMapper.toEntity(traineeDTO)).thenReturn(traineeEntity);

        gymFacade.createTrainee(traineeDTO);

        verify(traineeMapper).toEntity(traineeDTO);
        verify(traineeService).createTraineeProfile(traineeEntity);
    }

    @Test
    void testUpdateTraineeDelegatesToTraineeService() {
        when(traineeMapper.toEntity(traineeDTO)).thenReturn(traineeEntity);

        gymFacade.updateTrainee(traineeDTO);

        verify(traineeMapper).toEntity(traineeDTO);
        verify(traineeService).updateTraineeProfile(traineeEntity);
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
        when(traineeService.selectTraineeProfileById(1L)).thenReturn(traineeEntity);
        when(traineeMapper.toDTO(traineeEntity)).thenReturn(traineeDTO);

        TraineeDTO result = gymFacade.getTraineeById(1L);

        assertSame(traineeDTO, result);
    }

    @Test
    void testGetTraineeByUsernameReturnsTraineeFromService() {
        when(traineeService.selectTraineeProfileByUsername("john.doe")).thenReturn(traineeEntity);
        when(traineeMapper.toDTO(traineeEntity)).thenReturn(traineeDTO);

        TraineeDTO result = gymFacade.getTraineeByUsername("john.doe");

        assertSame(traineeDTO, result);
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
        List<Training> entities = List.of(trainingEntity);
        when(traineeService.getTrainingsForTrainee("john.doe", from, to, "trainer", "yoga")).thenReturn(entities);
        when(trainingMapper.toDTO(trainingEntity)).thenReturn(trainingDTO);

        List<TrainingDTO> result = gymFacade.getTrainingsForTrainee("john.doe", from, to, "trainer", "yoga");

        assertEquals(1, result.size());
        assertSame(trainingDTO, result.get(0));
    }

    @Test
    void testGetTrainersNotAssignedToTraineeDelegatesToTraineeService() {
        List<Trainer> entities = List.of(trainerEntity);
        when(traineeService.getTrainersNotAssignedToTrainee("john.doe")).thenReturn(entities);
        when(trainerMapper.toDTO(trainerEntity)).thenReturn(trainerDTO);

        List<TrainerDTO> result = gymFacade.getTrainersNotAssignedToTrainee("john.doe");

        assertEquals(1, result.size());
        assertSame(trainerDTO, result.get(0));
    }

    @Test
    void testUpdateTraineeListOfTrainersDelegatesToTraineeService() {
        List<String> usernames = List.of("trainer1", "trainer2");

        gymFacade.updateTraineeListOfTrainers(1L, usernames);

        verify(traineeService).updateTraineeListOfTrainers(1L, usernames);
    }

    @Test
    void testCreateTrainerDelegatesToTrainerService() {
        when(trainerMapper.toEntity(trainerDTO)).thenReturn(trainerEntity);

        gymFacade.createTrainer(trainerDTO);

        verify(trainerMapper).toEntity(trainerDTO);
        verify(trainerService).createTrainerProfile(trainerEntity);
    }

    @Test
    void testUpdateTrainerDelegatesToTrainerService() {
        when(trainerMapper.toEntity(trainerDTO)).thenReturn(trainerEntity);

        gymFacade.updateTrainer(trainerDTO);

        verify(trainerMapper).toEntity(trainerDTO);
        verify(trainerService).updateTrainerProfile(trainerEntity);
    }

    @Test
    void testGetTrainerByIdReturnsTrainerFromService() {
        when(trainerService.selectTrainerProfileById(2L)).thenReturn(trainerEntity);
        when(trainerMapper.toDTO(trainerEntity)).thenReturn(trainerDTO);

        TrainerDTO result = gymFacade.getTrainerById(2L);

        assertSame(trainerDTO, result);
    }

    @Test
    void testGetTrainerByUsernameReturnsTrainerFromService() {
        when(trainerService.selectTrainerProfileByUsername("jane.doe")).thenReturn(trainerEntity);
        when(trainerMapper.toDTO(trainerEntity)).thenReturn(trainerDTO);

        TrainerDTO result = gymFacade.getTrainerByUsername("jane.doe");

        assertSame(trainerDTO, result);
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
        List<Training> entities = List.of(trainingEntity);
        when(trainerService.getTrainingsForTrainer("jane.doe", from, to, "trainee")).thenReturn(entities);
        when(trainingMapper.toDTO(trainingEntity)).thenReturn(trainingDTO);

        List<TrainingDTO> result = gymFacade.getTrainingsForTrainer("jane.doe", from, to, "trainee");

        assertEquals(1, result.size());
        assertSame(trainingDTO, result.getFirst());
    }

    @Test
    void testGetTrainingReturnsTrainingFromService() {
        when(trainingService.selectTraining(3L)).thenReturn(trainingEntity);
        when(trainingMapper.toDTO(trainingEntity)).thenReturn(trainingDTO);

        TrainingDTO result = gymFacade.getTraining(3L);

        assertSame(trainingDTO, result);
    }

    @Test
    void testAddTrainingDelegatesToTrainingService() {
        LocalDate date = LocalDate.of(2024, 3, 15);

        gymFacade.addTraining("john.doe", "jane.doe", "Session 1", date, 60);

        verify(trainingService).addTraining("john.doe", "jane.doe", "Session 1", date, 60);
    }
}