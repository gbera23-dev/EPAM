import app.dto.api.response.TrainingTypeResponse;
import app.entities.TrainingType;
import app.mappers.api.TrainingTypeApiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import app.restcontroller.TrainingTypeRestController;
import app.services.AuthService;
import app.services.TrainingTypeService;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ExtendWith(MockitoExtension.class)
class TrainingTypeRestControllerTest {

    private MockMvc mockMvc;

    @Mock private TrainingTypeService trainingTypeService;
    @Mock private TrainingTypeApiMapper trainingTypeApiMapper;
    @Mock private AuthService authService;
    @InjectMocks
    private TrainingTypeRestController trainingTypeRestController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trainingTypeRestController).build();
    }

    @Test
    void testGetTrainingTypesReturnsListOf2() throws Exception {
        TrainingType yoga = new TrainingType(1L, "Yoga", List.of(), List.of());
        TrainingType pilates = new TrainingType(2L, "Pilates", List.of(), List.of());

        when(trainingTypeService.getTrainingTypes()).thenReturn(List.of(yoga, pilates));
        when(trainingTypeApiMapper.toTrainingTypeResponse(yoga))
                .thenReturn(new TrainingTypeResponse("Yoga", 1L));
        when(trainingTypeApiMapper.toTrainingTypeResponse(pilates))
                .thenReturn(new TrainingTypeResponse("Pilates", 2L));

        mockMvc.perform(get("/api/training-types")
                        .header("user-session", "john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].trainingTypeName").value("Yoga"))
                .andExpect(jsonPath("$[0].trainingTypeId").value(1))
                .andExpect(jsonPath("$[1].trainingTypeName").value("Pilates"))
                .andExpect(jsonPath("$[1].trainingTypeId").value(2));
    }

    @Test
    void testGetTrainingTypesReturnsEmptyList() throws Exception {
        when(trainingTypeService.getTrainingTypes()).thenReturn(List.of());

        mockMvc.perform(get("/api/training-types")
                        .header("user-session", "john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetTrainingTypesMapsEachTypeViaMapper() throws Exception {
        TrainingType yoga = new TrainingType(1L, "Yoga", List.of(), List.of());
        when(trainingTypeService.getTrainingTypes()).thenReturn(List.of(yoga));
        when(trainingTypeApiMapper.toTrainingTypeResponse(yoga))
                .thenReturn(new TrainingTypeResponse("Yoga", 1L));

        mockMvc.perform(get("/api/training-types")
                        .header("user-session", "john.doe"))
                .andExpect(status().isOk());

        verify(trainingTypeApiMapper, times(1)).toTrainingTypeResponse(yoga);
    }

}
