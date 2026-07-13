package com.example.Trainer_history_service;

import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.entities.ActionType;
import com.example.Trainer_history_service.errorHandler.GlobalExceptionHandler;
import com.example.Trainer_history_service.exceptions.MonthlySummaryNotFoundException;
import com.example.Trainer_history_service.exceptions.NegativeDurationException;
import com.example.Trainer_history_service.exceptions.UserNotFoundException;
import com.example.Trainer_history_service.restController.TrainerRestController;
import com.example.Trainer_history_service.services.TrainerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TrainerRestControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainerRestController trainerRestController;

    private static final String BASE_URL = "/api/trainer";
    private static final String BATCH_URL = "/api/trainer/in-batch";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders
                .standaloneSetup(trainerRestController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private TrainerWorkloadRequest buildRequest(ActionType actionType) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setUsername("john.doe");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setIsActive(true);
        request.setTrainingDate(LocalDate.of(2024, 6, 15));
        request.setDuration(60);
        request.setActionType(actionType);
        return request;
    }

    @Test
    void testUpdateTrainerWorkloadOnValidAddRequest() throws Exception {
        TrainerWorkloadRequest request = buildRequest(ActionType.ADD);
        doNothing().when(trainerService).updateTrainingHours(any(TrainerWorkloadRequest.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("trainer workload updated successfully!"));

        verify(trainerService, times(1)).updateTrainingHours(any(TrainerWorkloadRequest.class));
    }

    @Test
    void testUpdateTrainerWorkloadOnValidDeleteRequest() throws Exception {
        TrainerWorkloadRequest request = buildRequest(ActionType.DELETE);
        doNothing().when(trainerService).updateTrainingHours(any(TrainerWorkloadRequest.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("trainer workload updated successfully!"));

        verify(trainerService, times(1)).updateTrainingHours(any(TrainerWorkloadRequest.class));
    }

    @Test
    void testUpdateTrainerWorkloadOnUserNotFound() throws Exception {
        TrainerWorkloadRequest request = buildRequest(ActionType.ADD);
        doThrow(new UserNotFoundException("Trainer not found"))
                .when(trainerService).updateTrainingHours(any(TrainerWorkloadRequest.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Trainer not found"));
    }

    @Test
    void testUpdateTrainerWorkloadOnNegativeDuration() throws Exception {
        TrainerWorkloadRequest request = buildRequest(ActionType.ADD);
        request.setDuration(-10);
        doThrow(new NegativeDurationException("Duration cannot be negative"))
                .when(trainerService).updateTrainingHours(any(TrainerWorkloadRequest.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotAcceptable())
                .andExpect(content().string("Duration cannot be negative"));
    }

    @Test
    void testUpdateTrainerWorkloadOnUnexpectedServiceException() throws Exception {
        TrainerWorkloadRequest request = buildRequest(ActionType.ADD);
        doThrow(new RuntimeException("Unexpected error"))
                .when(trainerService).updateTrainingHours(any(TrainerWorkloadRequest.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unexpected error"));
    }

    @Test
    void testUpdateTrainersWorkloadInBatchOnValidRequests() throws Exception {
        List<TrainerWorkloadRequest> requests = List.of(
                buildRequest(ActionType.ADD),
                buildRequest(ActionType.DELETE)
        );
        doNothing().when(trainerService).updateTrainingHoursInBatch(anyList());

        mockMvc.perform(post(BATCH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isOk())
                .andExpect(content().string("Workloads have been updated successfully!"));

        verify(trainerService, times(1)).updateTrainingHoursInBatch(anyList());
    }

    @Test
    void testUpdateTrainersWorkloadInBatchOnEmptyList() throws Exception {
        doNothing().when(trainerService).updateTrainingHoursInBatch(anyList());

        mockMvc.perform(post(BATCH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(content().string("Workloads have been updated successfully!"));

        verify(trainerService, times(1)).updateTrainingHoursInBatch(anyList());
    }

    @Test
    void testUpdateTrainersWorkloadInBatchOnServiceException() throws Exception {
        List<TrainerWorkloadRequest> requests = List.of(buildRequest(ActionType.ADD));
        doThrow(new RuntimeException("Batch processing failed"))
                .when(trainerService).updateTrainingHoursInBatch(anyList());

        mockMvc.perform(post(BATCH_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Batch processing failed"));
    }

    @Test
    void testGetTrainerHoursOnValidUsernameAndDate() throws Exception {
        when(trainerService.getTrainingHours(eq("john.doe"), eq(LocalDate.of(2024, 6, 15))))
                .thenReturn(90);

        mockMvc.perform(get(BASE_URL)
                        .param("username", "john.doe")
                        .param("date", "2024-06-15"))
                .andExpect(status().isOk())
                .andExpect(content().string("90"));

        verify(trainerService, times(1)).getTrainingHours("john.doe", LocalDate.of(2024, 6, 15));
    }

    @Test
    void testGetTrainerHoursOnZeroHours() throws Exception {
        when(trainerService.getTrainingHours(eq("john.doe"), any(LocalDate.class)))
                .thenReturn(0);

        mockMvc.perform(get(BASE_URL)
                        .param("username", "john.doe")
                        .param("date", "2024-06-15"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    void testGetTrainerHoursOnUserNotFound() throws Exception {
        when(trainerService.getTrainingHours(eq("unknown.user"), any(LocalDate.class)))
                .thenThrow(new UserNotFoundException("Trainer not found"));

        mockMvc.perform(get(BASE_URL)
                        .param("username", "unknown.user")
                        .param("date", "2024-06-15"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Trainer not found"));
    }

    @Test
    void testGetTrainerHoursOnMonthlySummaryNotFound() throws Exception {
        when(trainerService.getTrainingHours(eq("john.doe"), any(LocalDate.class)))
                .thenThrow(new MonthlySummaryNotFoundException("No training record found for given date"));

        mockMvc.perform(get(BASE_URL)
                        .param("username", "john.doe")
                        .param("date", "2024-06-15"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No training record found for given date"));
    }

    @Test
    void testGetTrainerHoursOnUnexpectedServiceException() throws Exception {
        when(trainerService.getTrainingHours(any(), any()))
                .thenThrow(new RuntimeException("Database unavailable"));

        mockMvc.perform(get(BASE_URL)
                        .param("username", "john.doe")
                        .param("date", "2024-06-15"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Database unavailable"));
    }
}