import app.Application;
import app.dto.api.request.*;
import app.dto.api.response.*;
import app.entities.*;
import app.mappers.api.TraineeApiMapper;
import app.mappers.api.TrainerApiMapper;
import app.mappers.api.TrainingApiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import app.restcontroller.TraineeRestController;
import app.services.AuthService;
import app.services.TraineeService;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TraineeRestControllerTest {

    private MockMvc mockMvc;

    @Mock private TraineeService traineeService;
    @Mock private TraineeApiMapper traineeApiMapper;
    @Mock private TrainerApiMapper trainerApiMapper;
    @Mock private TrainingApiMapper trainingApiMapper;
    @Mock private AuthService authService;

    @InjectMocks
    private TraineeRestController traineeRestController;

    private Trainee trainee;
    private User user;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(traineeRestController).build();
        user = new User();
        user.setId(1L);
        user.setUsername("john.doe");
        user.setPassword("pass123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setActive(true);

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(user);
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 15));
        trainee.setAddress("123 Main St");
        trainee.setTrainers(List.of());
        trainee.setTrainings(List.of());

    }

    @Test
    void testRegisterTraineeReturns201WithCredentials() throws Exception {
        when(traineeApiMapper.toTrainee(any(TraineeRegistrationRequest.class))).thenReturn(trainee);
        doNothing().when(traineeService).createTraineeProfile(trainee);

        mockMvc.perform(post("/api/trainee/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "John",
                                  "lastName": "Doe",
                                  "dateOfBirth": "1990-01-15",
                                  "address": "123 Main St"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.password").value("pass123"));

        verify(traineeService).createTraineeProfile(any(Trainee.class));
    }

    @Test
    void testRegisterTraineeMissingFirstNameReturns400() throws Exception {
        mockMvc.perform(post("/api/trainee/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "lastName": "Doe"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetTraineeReturns200WithProfile() throws Exception {
        TraineeProfileResponse profileResponse = new TraineeProfileResponse(
                "John", "Doe", LocalDate.of(1990, 1, 15), "123 Main St", true, List.of()
        );

        when(traineeService.selectTraineeProfileByUsername("john.doe")).thenReturn(trainee);
        when(traineeApiMapper.toTraineeProfileResponse(trainee)).thenReturn(profileResponse);

        mockMvc.perform(get("/api/trainee")
                        .param("username", "john.doe")
                        .header("user-session", "john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.active").value(true));
    }


    @Test
    void testUpdateTraineeReturns200WithUpdatedProfile() throws Exception {
        TraineeProfileResponse profileResponse = new TraineeProfileResponse(
                "Jane", "Doe", LocalDate.of(1990, 1, 15), "456 New Ave", true, List.of()
        );

        when(traineeService.selectTraineeProfileByUsername("john.doe")).thenReturn(trainee);
        when(traineeApiMapper.toTraineeProfileResponse(trainee)).thenReturn(profileResponse);

        mockMvc.perform(put("/api/trainee/update")
                        .header("user-session", "john.doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "john.doe",
                                  "firstName": "Jane",
                                  "lastName": "Doe",
                                  "address": "456 New Ave",
                                  "isActive": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.address").value("456 New Ave"));

        verify(traineeService).updateTraineeProfile(trainee);
    }

    @Test
    void testUpdateTraineeSetsUserFieldsFromRequest() throws Exception {
        when(traineeService.selectTraineeProfileByUsername("john.doe")).thenReturn(trainee);
        when(traineeApiMapper.toTraineeProfileResponse(trainee))
                .thenReturn(new TraineeProfileResponse("Jane", "Smith",
                        null, null, false, List.of()));

        mockMvc.perform(put("/api/trainee/update")
                        .header("user-session", "john.doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "john.doe",
                                  "firstName": "Jane",
                                  "lastName": "Smith",
                                  "isActive": false
                                }
                                """))
                .andExpect(status().isOk());

        verify(traineeService).updateTraineeProfile(argThat(t ->
                t.getUser().getFirstName().equals("Jane") &&
                t.getUser().getLastName().equals("Smith") &&
                !t.getUser().isActive()
        ));
    }

    @Test
    void testDeleteTraineeReturns200WithMessage() throws Exception {
        doNothing().when(traineeService).deleteTraineeProfileByUsername("john.doe");

        mockMvc.perform(delete("/api/trainee")
                        .param("username", "john.doe")
                        .header("user-session", "john.doe"))
                .andExpect(status().isOk())
                .andExpect(content().string("Trainee was deleted successfully!"));

        verify(traineeService).deleteTraineeProfileByUsername("john.doe");
    }

    @Test
    void testGetNotAssignedTrainersReturnsListOf2() throws Exception {
        TrainingType trainingType = new TrainingType(1L, "Yoga", List.of(), List.of());

        User trainerUser1 = new User();
        trainerUser1.setUsername("trainer.one");
        trainerUser1.setFirstName("Alice");
        trainerUser1.setLastName("Smith");

        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        trainer1.setUser(trainerUser1);
        trainer1.setTrainingType(trainingType);
        trainer1.setTrainees(List.of());

        User trainerUser2 = new User();
        trainerUser2.setUsername("trainer.two");
        trainerUser2.setFirstName("Bob");
        trainerUser2.setLastName("Jones");

        Trainer trainer2 = new Trainer();
        trainer2.setId(2L);
        trainer2.setUser(trainerUser2);
        trainer2.setTrainingType(trainingType);
        trainer2.setTrainees(List.of());

        TrainingTypeResponse typeResponse = new TrainingTypeResponse("Yoga", 1L);
        TrainerSummaryResponse summary1 = new TrainerSummaryResponse("trainer.one", "Alice", "Smith", typeResponse);
        TrainerSummaryResponse summary2 = new TrainerSummaryResponse("trainer.two", "Bob", "Jones", typeResponse);

        when(traineeService.getTrainersNotAssignedToTrainee("john.doe"))
                .thenReturn(List.of(trainer1, trainer2));
        when(trainerApiMapper.toTrainerSummaryResponse(trainer1)).thenReturn(summary1);
        when(trainerApiMapper.toTrainerSummaryResponse(trainer2)).thenReturn(summary2);

        mockMvc.perform(get("/api/trainee/not-assigned-trainers")
                        .param("username", "john.doe")
                        .header("user-session", "john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("trainer.one"))
                .andExpect(jsonPath("$[1].username").value("trainer.two"));
    }

    @Test
    void testGetNotAssignedTrainersReturnsEmptyList() throws Exception {
        when(traineeService.getTrainersNotAssignedToTrainee("john.doe")).thenReturn(List.of());

        mockMvc.perform(get("/api/trainee/not-assigned-trainers")
                        .param("username", "john.doe")
                        .header("user-session", "john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testUpdateTraineeTrainersListReturnsUpdatedList() throws Exception {
        TrainingType trainingType = new TrainingType(1L, "Yoga", List.of(), List.of());

        User trainerUser = new User();
        trainerUser.setUsername("trainer.one");
        trainerUser.setFirstName("Alice");
        trainerUser.setLastName("Smith");

        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(trainerUser);
        trainer.setTrainingType(trainingType);
        trainer.setTrainees(List.of());

        TrainingTypeResponse typeResponse = new TrainingTypeResponse("Yoga", 1L);
        TrainerSummaryResponse summary = new TrainerSummaryResponse("trainer.one", "Alice", "Smith", typeResponse);

        when(traineeService.selectTraineeProfileByUsername("john.doe")).thenReturn(trainee);
        when(traineeService.getTrainersAssignedToTrainee("john.doe")).thenReturn(List.of(trainer));
        when(trainerApiMapper.toTrainerSummaryResponse(trainer)).thenReturn(summary);

        mockMvc.perform(put("/api/trainee/update-trainers")
                        .header("user-session", "john.doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "traineeUsername": "john.doe",
                                  "trainerUsernames": ["trainer.one"]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("trainer.one"));

        verify(traineeService).updateTraineeListOfTrainers(1L, List.of("trainer.one"));
    }

    @Test
    void testGetTraineeTrainingsReturnsFilteredList() throws Exception {
        TrainingType trainingType = new TrainingType(1L, "Yoga", List.of(), List.of());

        User trainerUser = new User();
        trainerUser.setFirstName("Alice");
        trainerUser.setLastName("Smith");

        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setTrainingType(trainingType);
        trainer.setTrainees(List.of());

        Training training = new Training();
        training.setId(1L);
        training.setName("Morning Yoga");
        training.setDate(LocalDate.of(2025, 6, 1));
        training.setDuration(60);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);

        TrainingResponse trainingResponse = new TrainingResponse(
                "Morning Yoga", LocalDate.of(2025, 6, 1),
                new TrainingTypeResponse("Yoga", 1L), 60, "Alice Smith"
        );

        when(traineeService.getTrainingsForTrainee(
                eq("john.doe"), any(), any(), any(), any()))
                .thenReturn(List.of(training));
        when(trainingApiMapper.toTrainingResponse(training)).thenReturn(trainingResponse);

        mockMvc.perform(get("/api/trainee/trainings")
                        .header("user-session", "john.doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "john.doe",
                                  "from": "2025-01-01",
                                  "to": "2025-12-31",
                                  "trainerName": "Alice",
                                  "trainingTypeRequest": { "trainingTypeName": "Yoga", "trainingTypeId": 1 }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].trainingName").value("Morning Yoga"))
                .andExpect(jsonPath("$[0].duration").value(60));
    }

    @Test
    void testActivateTraineeReturns200WithMessage() throws Exception {
        when(traineeService.selectTraineeProfileByUsername("john.doe")).thenReturn(trainee);

        mockMvc.perform(patch("/api/trainee/john.doe/status")
                        .header("user-session", "john.doe")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("trainee has been successfully activated!"));

        verify(traineeService).activateTraineeProfile(1L);
        verify(traineeService, never()).deactivateTraineeProfile(anyLong());
    }

    @Test
    void testDeactivateTraineeReturns200WithMessage() throws Exception {
        when(traineeService.selectTraineeProfileByUsername("john.doe")).thenReturn(trainee);

        mockMvc.perform(patch("/api/trainee/john.doe/status")
                        .header("user-session", "john.doe")
                        .param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string("trainee has been successfully deactivated!"));

        verify(traineeService).deactivateTraineeProfile(1L);
        verify(traineeService, never()).activateTraineeProfile(anyLong());
    }
}
