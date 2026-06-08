import app.dto.api.request.*;
import app.dto.api.response.*;
import app.entities.*;
import app.mappers.api.TrainerApiMapper;
import app.mappers.api.TrainingApiMapper;
import app.restcontroller.TrainerRestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import app.services.AuthService;
import app.services.TrainerService;
import app.services.TrainingTypeService;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class TrainerRestControllerTest {
    
    private MockMvc mockMvc;

    @Mock
    private TrainerService trainerService;
    @Mock private TrainerApiMapper trainerApiMapper;
    @Mock private TrainingApiMapper trainingApiMapper;
    @Mock
    private TrainingTypeService trainingTypeService;
    @Mock private AuthService authService;

    @InjectMocks
    TrainerRestController trainerRestController;

    private Trainer trainer;
    private TrainingType trainingType;
    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainerRestController).build();
        user = new User();
        user.setId(1L);
        user.setUsername("trainer.one");
        user.setPassword("secret");
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setActive(true);

        trainingType = new TrainingType(1L, "Yoga", List.of(), List.of());

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(user);
        trainer.setTrainingType(trainingType);
        trainer.setTrainees(List.of());
        trainer.setTrainings(List.of());

    }

    @Test
    void testRegisterTrainerReturns201WithCredentials() throws Exception {
        when(trainingTypeService.getTrainingTypeById(1L)).thenReturn(trainingType);
        when(trainerApiMapper.toTrainer(any(TrainerRegistrationRequest.class), eq(trainingType)))
                .thenReturn(trainer);
        doNothing().when(trainerService).createTrainerProfile(trainer);

        mockMvc.perform(post("/api/trainer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Alice",
                                  "lastName": "Smith",
                                  "specialization": { "trainingTypeId": 1 }
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("trainer.one"))
                .andExpect(jsonPath("$.password").value("secret"));

        verify(trainerService).createTrainerProfile(any(Trainer.class));
    }

    @Test
    void testGetTrainerReturns200WithProfile() throws Exception {
        TrainingTypeResponse typeResponse = new TrainingTypeResponse("Yoga", 1L);
        TrainerProfileResponse profileResponse = new TrainerProfileResponse(
                "Alice", "Smith", typeResponse, true, List.of()
        );

        when(trainerService.selectTrainerProfileByUsername("trainer.one")).thenReturn(trainer);
        when(trainerApiMapper.toTrainerProfileResponse(trainer)).thenReturn(profileResponse);

        mockMvc.perform(get("/api/trainer")
                        .param("username", "trainer.one")
                        .header("user-session", "trainer.one"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testUpdateTrainerReturns200WithUpdatedProfile() throws Exception {
        TrainingTypeResponse typeResponse = new TrainingTypeResponse("Yoga", 1L);
        TrainerProfileResponse profileResponse = new TrainerProfileResponse(
                "Alice", "Updated", typeResponse, true, List.of()
        );

        when(trainerService.selectTrainerProfileByUsername("trainer.one")).thenReturn(trainer);
        when(trainerApiMapper.toTrainerProfileResponse(trainer)).thenReturn(profileResponse);

        mockMvc.perform(put("/api/trainer/update")
                        .header("user-session", "trainer.one")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "trainer.one",
                                  "firstName": "Alice",
                                  "lastName": "Updated",
                                  "isActive": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Updated"));

        verify(trainerService).updateTrainerProfile(trainer);
    }

    @Test
    void testUpdateTrainerChangesSpecialization() throws Exception {
        TrainingType newType = new TrainingType(2L, "Pilates", List.of(), List.of());
        when(trainerService.selectTrainerProfileByUsername("trainer.one")).thenReturn(trainer);
        when(trainingTypeService.getTrainingTypeById(2L)).thenReturn(newType);
        when(trainerApiMapper.toTrainerProfileResponse(trainer))
                .thenReturn(new TrainerProfileResponse("Alice", "Smith",
                        new TrainingTypeResponse("Pilates", 2L), true, List.of()));

        mockMvc.perform(put("/api/trainer/update")
                        .header("user-session", "trainer.one")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "trainer.one",
                                  "firstName": "Alice",
                                  "lastName": "Smith",
                                  "isActive": true,
                                  "specialization": { "trainingTypeId": 2 }
                                }
                                """))
                .andExpect(status().isOk());

        verify(trainerService).updateTrainerProfile(argThat(t ->
                t.getTrainingType().getName().equals("Pilates")
        ));
    }

    @Test
    void testGetTrainerTrainingsReturnsFilteredList() throws Exception {
        User traineeUser = new User();
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");

        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        Training training = new Training();
        training.setId(1L);
        training.setName("Evening Yoga");
        training.setDate(LocalDate.of(2025, 5, 10));
        training.setDuration(45);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainee(trainee);

        TrainingResponse trainingResponse = new TrainingResponse(
                "Evening Yoga", LocalDate.of(2025, 5, 10),
                new TrainingTypeResponse("Yoga", 1L), 45, "Alice Smith"
        );

        when(trainerService.getTrainingsForTrainer(
                eq("trainer.one"), any(), any(), any()))
                .thenReturn(List.of(training));
        when(trainingApiMapper.toTrainingResponse(training)).thenReturn(trainingResponse);

        mockMvc.perform(get("/api/trainer/trainings")
                        .header("user-session", "trainer.one")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "trainer.one",
                                  "from": "2025-01-01",
                                  "to": "2025-12-31",
                                  "traineeName": "John"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].trainingName").value("Evening Yoga"))
                .andExpect(jsonPath("$[0].duration").value(45));
    }

    @Test
    void testGetTrainerTrainingsReturnsEmptyList() throws Exception {
        when(trainerService.getTrainingsForTrainer(any(), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/trainer/trainings")
                        .header("user-session", "trainer.one")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "trainer.one"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testActivateTrainerReturns200WithMessage() throws Exception {
        when(trainerService.selectTrainerProfileByUsername("trainer.one")).thenReturn(trainer);

        mockMvc.perform(patch("/api/trainer/trainer.one/status")
                        .header("user-session", "trainer.one")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("trainer has been successfully activated!"));

        verify(trainerService).activateTrainerProfile(1L);
        verify(trainerService, never()).deactivateTrainerProfile(anyLong());
    }

    @Test
    void testDeactivateTrainerReturns200WithMessage() throws Exception {
        when(trainerService.selectTrainerProfileByUsername("trainer.one")).thenReturn(trainer);

        mockMvc.perform(patch("/api/trainer/trainer.one/status")
                        .header("user-session", "trainer.one")
                        .param("active", "false"))
                .andExpect(status().isOk())
                .andExpect(content().string("trainer has been successfully deactivated!"));

        verify(trainerService).deactivateTrainerProfile(1L);
        verify(trainerService, never()).activateTrainerProfile(anyLong());
    }
}
