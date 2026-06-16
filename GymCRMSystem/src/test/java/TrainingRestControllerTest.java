import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import app.restcontroller.TrainingRestController;
import app.services.AuthService;
import app.services.TrainingService;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TrainingRestControllerTest {

    private MockMvc mockMvc;

    @Mock private TrainingService trainingService;
    @Mock
    private AuthService authService;
    @InjectMocks
    private TrainingRestController trainingRestController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainingRestController).build();
    }

    @Test
    void testAddTrainingReturns200WithSuccessMessage() throws Exception {
        doNothing().when(trainingService).addTraining(any(), any(), any(), any(), anyInt());

        mockMvc.perform(post("/api/trainings")
                        .header("user-session", "john.doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "traineeUsername": "john.doe",
                                  "trainerUsername": "trainer.one",
                                  "trainingName": "Morning Yoga",
                                  "date": "2025-06-01",
                                  "duration": 60
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string("Training was added successfully!"));
    }

    @Test
    void testAddTrainingDelegatesCorrectArgsToService() throws Exception {
        doNothing().when(trainingService).addTraining(any(), any(), any(), any(), anyInt());

        mockMvc.perform(post("/api/trainings")
                        .header("user-session", "john.doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "traineeUsername": "john.doe",
                                  "trainerUsername": "trainer.one",
                                  "trainingName": "Morning Yoga",
                                  "date": "2025-06-01",
                                  "duration": 60
                                }
                                """))
                .andExpect(status().isOk());

        verify(trainingService).addTraining(
                "john.doe",
                "trainer.one",
                "Morning Yoga",
                LocalDate.of(2025, 6, 1),
                60
        );
    }

    @Test
    void testAddTrainingMissingTraineeUsernameReturns400() throws Exception {
        mockMvc.perform(post("/api/trainings")
                        .header("user-session", "john.doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "trainerUsername": "trainer.one",
                                  "trainingName": "Morning Yoga",
                                  "date": "2025-06-01",
                                  "duration": 60
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(trainingService, never()).addTraining(any(), any(), any(), any(), anyInt());
    }

    @Test
    void testAddTrainingMissingDateReturns400() throws Exception {
        mockMvc.perform(post("/api/trainings")
                        .header("user-session", "john.doe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "traineeUsername": "john.doe",
                                  "trainerUsername": "trainer.one",
                                  "trainingName": "Morning Yoga",
                                  "duration": 60
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(trainingService, never()).addTraining(any(), any(), any(), any(), anyInt());
    }
}
