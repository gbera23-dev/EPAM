package com.example.Trainer_history_service;

import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.entities.ActionType;
import com.example.Trainer_history_service.entities.MonthlySummary;
import com.example.Trainer_history_service.entities.TrainerWorkload;
import com.example.Trainer_history_service.exceptions.MonthlySummaryNotFoundException;
import com.example.Trainer_history_service.exceptions.NegativeDurationException;
import com.example.Trainer_history_service.exceptions.UserNotFoundException;
import com.example.Trainer_history_service.repository.MonthlySummaryRepository;
import com.example.Trainer_history_service.repository.TrainerWorkloadRepository;
import com.example.Trainer_history_service.services.TrainerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @Mock
    private MonthlySummaryRepository monthlySummaryRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private static final String USERNAME = "john.doe";
    private static final LocalDate DATE = LocalDate.of(2024, 6, 15);
    private static final LocalDate MONTH_START = LocalDate.of(2024, 6, 1);

    private TrainerWorkload buildWorkload() {
        return new TrainerWorkload(1L, USERNAME, "John", "Doe", true, Collections.emptyList());
    }

    private MonthlySummary buildMonthlySummary(TrainerWorkload workload, int duration) {
        return new MonthlySummary(1L, MONTH_START, duration, workload);
    }

    private TrainerWorkloadRequest buildRequest(ActionType actionType, int duration) {
        TrainerWorkloadRequest request = new TrainerWorkloadRequest();
        request.setUsername(USERNAME);
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setIsActive(true);
        request.setTrainingDate(DATE);
        request.setDuration(duration);
        request.setActionType(actionType);
        return request;
    }

    @Test
    void testGetTrainingHoursSuccess() {
        TrainerWorkload workload = buildWorkload();
        MonthlySummary summary = buildMonthlySummary(workload, 10);

        when(trainerWorkloadRepository.findByUsername(USERNAME)).thenReturn(Optional.of(workload));
        when(monthlySummaryRepository.findByTrainerWorkloadIdAndDate(1L, MONTH_START))
                .thenReturn(Optional.of(summary));

        Integer result = trainerService.getTrainingHours(USERNAME, DATE);

        assertEquals(10, result);
    }

    @Test
    void testGetTrainingHoursUserNotFound() {
        when(trainerWorkloadRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainerService.getTrainingHours(USERNAME, DATE));
    }

    @Test
    void testGetTrainingHoursMonthlySummaryNotFound() {
        TrainerWorkload workload = buildWorkload();

        when(trainerWorkloadRepository.findByUsername(USERNAME)).thenReturn(Optional.of(workload));
        when(monthlySummaryRepository.findByTrainerWorkloadIdAndDate(any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(MonthlySummaryNotFoundException.class,
                () -> trainerService.getTrainingHours(USERNAME, DATE));
    }

    @Test
    void testUpdateTrainingHoursNewWorkloadIsCreated() {
        TrainerWorkloadRequest request = buildRequest(ActionType.ADD, 5);
        TrainerWorkload workload = buildWorkload();

        when(trainerWorkloadRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(trainerWorkloadRepository.findByUsername(USERNAME)).thenReturn(Optional.of(workload));
        when(monthlySummaryRepository.findByTrainerWorkloadIdAndDate(any(), any()))
                .thenReturn(Optional.empty());

        trainerService.updateTrainingHours(request);

        verify(trainerWorkloadRepository).save(any(TrainerWorkload.class));
    }

    @Test
    void testUpdateTrainingHoursExistingWorkloadNotCreatedAgain() {
        TrainerWorkloadRequest request = buildRequest(ActionType.ADD, 5);
        TrainerWorkload workload = buildWorkload();

        when(trainerWorkloadRepository.existsByUsername(USERNAME)).thenReturn(true);
        when(trainerWorkloadRepository.findByUsername(USERNAME)).thenReturn(Optional.of(workload));
        when(monthlySummaryRepository.findByTrainerWorkloadIdAndDate(any(), any()))
                .thenReturn(Optional.empty());

        trainerService.updateTrainingHours(request);

        verify(trainerWorkloadRepository, never()).save(any(TrainerWorkload.class));
    }

    @Test
    void testUpdateTrainingHoursAddActionIncreasesExistingDuration() {
        TrainerWorkloadRequest request = buildRequest(ActionType.ADD, 5);
        TrainerWorkload workload = buildWorkload();
        MonthlySummary existing = buildMonthlySummary(workload, 10);

        when(trainerWorkloadRepository.existsByUsername(USERNAME)).thenReturn(true);
        when(trainerWorkloadRepository.findByUsername(USERNAME)).thenReturn(Optional.of(workload));
        when(monthlySummaryRepository.findByTrainerWorkloadIdAndDate(any(), any()))
                .thenReturn(Optional.of(existing));

        trainerService.updateTrainingHours(request);

        assertEquals(15, existing.getDuration());
        verify(monthlySummaryRepository).save(existing);
    }

    @Test
    void testUpdateTrainingHoursAddActionSetsCorrectDurationForNewSummary() {
        TrainerWorkloadRequest request = buildRequest(ActionType.ADD, 5);
        TrainerWorkload workload = buildWorkload();

        when(trainerWorkloadRepository.existsByUsername(USERNAME)).thenReturn(true);
        when(trainerWorkloadRepository.findByUsername(USERNAME)).thenReturn(Optional.of(workload));
        when(monthlySummaryRepository.findByTrainerWorkloadIdAndDate(any(), any()))
                .thenReturn(Optional.empty());

        trainerService.updateTrainingHours(request);

        ArgumentCaptor<MonthlySummary> captor = ArgumentCaptor.forClass(MonthlySummary.class);
        verify(monthlySummaryRepository).save(captor.capture());
        assertEquals(5, captor.getValue().getDuration());
    }

    @Test
    void testUpdateTrainingHoursDeleteActionDecreasesExistingDuration() {
        TrainerWorkloadRequest request = buildRequest(ActionType.DELETE, 3);
        TrainerWorkload workload = buildWorkload();
        MonthlySummary existing = buildMonthlySummary(workload, 10);

        when(trainerWorkloadRepository.existsByUsername(USERNAME)).thenReturn(true);
        when(trainerWorkloadRepository.findByUsername(USERNAME)).thenReturn(Optional.of(workload));
        when(monthlySummaryRepository.findByTrainerWorkloadIdAndDate(any(), any()))
                .thenReturn(Optional.of(existing));

        trainerService.updateTrainingHours(request);

        assertEquals(7, existing.getDuration());
        verify(monthlySummaryRepository).save(existing);
    }

    @Test
    void testUpdateTrainingHoursDeleteActionResultingZeroDeletesSummary() {
        TrainerWorkloadRequest request = buildRequest(ActionType.DELETE, 10);
        TrainerWorkload workload = buildWorkload();
        MonthlySummary existing = buildMonthlySummary(workload, 10);

        when(trainerWorkloadRepository.existsByUsername(USERNAME)).thenReturn(true);
        when(trainerWorkloadRepository.findByUsername(USERNAME)).thenReturn(Optional.of(workload));
        when(monthlySummaryRepository.findByTrainerWorkloadIdAndDate(any(), any()))
                .thenReturn(Optional.of(existing));

        trainerService.updateTrainingHours(request);

        verify(monthlySummaryRepository).delete(existing);
        verify(monthlySummaryRepository, never()).save(any());
    }

    @Test
    void testUpdateTrainingHoursDeleteActionNegativeResultThrowsException() {
        TrainerWorkloadRequest request = buildRequest(ActionType.DELETE, 15);
        TrainerWorkload workload = buildWorkload();
        MonthlySummary existing = buildMonthlySummary(workload, 10);

        when(trainerWorkloadRepository.existsByUsername(USERNAME)).thenReturn(true);
        when(trainerWorkloadRepository.findByUsername(USERNAME)).thenReturn(Optional.of(workload));
        when(monthlySummaryRepository.findByTrainerWorkloadIdAndDate(any(), any()))
                .thenReturn(Optional.of(existing));

        assertThrows(NegativeDurationException.class,
                () -> trainerService.updateTrainingHours(request));
    }

    @Test
    void testUpdateTrainingHoursNegativeHoursThrowsException() {
        TrainerWorkloadRequest request = buildRequest(ActionType.ADD, -5);
        TrainerWorkload workload = buildWorkload();

        when(trainerWorkloadRepository.existsByUsername(USERNAME)).thenReturn(true);

        assertThrows(NegativeDurationException.class,
                () -> trainerService.updateTrainingHours(request));
    }

    @Test
    void testUpdateTrainingHoursInBatchProcessesAllRequests() {
        TrainerWorkload workload = buildWorkload();
        List<TrainerWorkloadRequest> requests = List.of(
                buildRequest(ActionType.ADD, 5),
                buildRequest(ActionType.ADD, 3)
        );

        when(trainerWorkloadRepository.existsByUsername(USERNAME)).thenReturn(true);
        when(trainerWorkloadRepository.findByUsername(USERNAME)).thenReturn(Optional.of(workload));
        when(monthlySummaryRepository.findByTrainerWorkloadIdAndDate(any(), any()))
                .thenReturn(Optional.empty());

        trainerService.updateTrainingHoursInBatch(requests);

        verify(monthlySummaryRepository, times(1)).save(any(MonthlySummary.class));
    }

    @Test
    void testUpdateTrainingHoursInBatchCreatesNewWorkloadForUnknownTrainer() {
        TrainerWorkload workload = buildWorkload();
        TrainerWorkloadRequest request = buildRequest(ActionType.ADD, 5);

        when(trainerWorkloadRepository.existsByUsername(USERNAME)).thenReturn(false);
        when(trainerWorkloadRepository.findByUsername(USERNAME)).thenReturn(Optional.of(workload));
        when(monthlySummaryRepository.findByTrainerWorkloadIdAndDate(any(), any()))
                .thenReturn(Optional.empty());

        trainerService.updateTrainingHoursInBatch(List.of(request));

        verify(trainerWorkloadRepository).save(any(TrainerWorkload.class));
    }

    @Test
    void testUpdateTrainingHoursInBatchSkipsWorkloadCreationForExistingTrainer() {
        TrainerWorkload workload = buildWorkload();
        TrainerWorkloadRequest request = buildRequest(ActionType.ADD, 5);

        when(trainerWorkloadRepository.existsByUsername(USERNAME)).thenReturn(true);
        when(trainerWorkloadRepository.findByUsername(USERNAME)).thenReturn(Optional.of(workload));
        when(monthlySummaryRepository.findByTrainerWorkloadIdAndDate(any(), any()))
                .thenReturn(Optional.empty());

        trainerService.updateTrainingHoursInBatch(List.of(request));

        verify(trainerWorkloadRepository, never()).save(any(TrainerWorkload.class));
    }

    @Test
    void testUpdateTrainingHoursMonthlySummaryDateNormalizedToFirstOfMonth() {
        TrainerWorkloadRequest request = buildRequest(ActionType.ADD, 5);
        TrainerWorkload workload = buildWorkload();

        when(trainerWorkloadRepository.existsByUsername(USERNAME)).thenReturn(true);
        when(trainerWorkloadRepository.findByUsername(USERNAME)).thenReturn(Optional.of(workload));
        when(monthlySummaryRepository.findByTrainerWorkloadIdAndDate(any(), any()))
                .thenReturn(Optional.empty());

        trainerService.updateTrainingHours(request);

        verify(monthlySummaryRepository).findByTrainerWorkloadIdAndDate(1L, MONTH_START);
    }
}
