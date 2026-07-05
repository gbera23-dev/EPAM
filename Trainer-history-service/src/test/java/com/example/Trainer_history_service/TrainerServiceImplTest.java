package com.example.Trainer_history_service;

import com.example.Trainer_history_service.entities.MonthlySummery;
import com.example.Trainer_history_service.entities.TrainerWorkload;
import com.example.Trainer_history_service.exceptions.MonthlySummaryNotFoundException;
import com.example.Trainer_history_service.exceptions.NegativeDurationException;
import com.example.Trainer_history_service.exceptions.UserNotFoundException;
import com.example.Trainer_history_service.repository.MonthlySummeryRepository;
import com.example.Trainer_history_service.repository.TrainerWorkloadRepository;
import com.example.Trainer_history_service.services.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerWorkloadRepository trainerWorkloadRepository;

    @Mock
    private MonthlySummeryRepository monthlySummeryRepository;

    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerServiceImpl(trainerWorkloadRepository, monthlySummeryRepository);
    }

    @Test
    void testCreateNewWorkloadSavesTrainerWorkload() {
        trainerService.createNewWorkload("john", "John", "Doe", true);

        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(trainerWorkloadRepository).save(captor.capture());

        TrainerWorkload saved = captor.getValue();
        assertEquals("john", saved.getUsername());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertTrue(saved.isActive());
    }

    @Test
    void testGetTrainingHoursReturnsHoursWhenFound() {
        TrainerWorkload workload = new TrainerWorkload(1L, "john", "John", "Doe", true, Collections.emptyList());
        MonthlySummery summary = new MonthlySummery(1L, LocalDate.of(2026, 7, 1), 120, workload);

        when(trainerWorkloadRepository.findByUsername("john")).thenReturn(Optional.of(workload));
        when(monthlySummeryRepository.findByIdAndDate(eq(1L), eq(LocalDate.of(2026, 7, 1))))
                .thenReturn(Optional.of(summary));

        Integer hours = trainerService.getTrainingHours("john", LocalDate.of(2026, 7, 15));

        assertEquals(120, hours);
    }

    @Test
    void testGetTrainingHoursThrowsUserNotFoundExceptionWhenTrainerNotFound() {
        when(trainerWorkloadRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainerService.getTrainingHours("john", LocalDate.of(2026, 7, 15)));
    }

    @Test
    void testGetTrainingHoursThrowsMonthlySummaryNotFoundExceptionWhenSummaryNotFound() {
        TrainerWorkload workload = new TrainerWorkload(1L, "john", "John", "Doe", true, Collections.emptyList());
        when(trainerWorkloadRepository.findByUsername("john")).thenReturn(Optional.of(workload));
        when(monthlySummeryRepository.findByIdAndDate(any(), any())).thenReturn(Optional.empty());

        assertThrows(MonthlySummaryNotFoundException.class,
                () -> trainerService.getTrainingHours("john", LocalDate.of(2026, 7, 15)));
    }

    @Test
    void testAddTrainingHoursAddsToExistingSummary() {
        TrainerWorkload workload = new TrainerWorkload(1L, "john", "John", "Doe", true, Collections.emptyList());
        MonthlySummery summary = new MonthlySummery(1L, LocalDate.of(2026, 7, 1), 60, workload);

        when(trainerWorkloadRepository.findByUsername("john")).thenReturn(Optional.of(workload));
        when(monthlySummeryRepository.findByIdAndDate(eq(1L), eq(LocalDate.of(2026, 7, 1))))
                .thenReturn(Optional.of(summary));

        trainerService.addTrainingHours("john", LocalDate.of(2026, 7, 10), 30);

        ArgumentCaptor<MonthlySummery> captor = ArgumentCaptor.forClass(MonthlySummery.class);
        verify(monthlySummeryRepository).save(captor.capture());
        assertEquals(90, captor.getValue().getDuration());
    }

    @Test
    void testAddTrainingHoursCreatesNewSummaryWhenNotFound() {
        TrainerWorkload workload = new TrainerWorkload(1L, "john", "John", "Doe", true, Collections.emptyList());

        when(trainerWorkloadRepository.findByUsername("john")).thenReturn(Optional.of(workload));
        when(monthlySummeryRepository.findByIdAndDate(any(), any())).thenReturn(Optional.empty());

        trainerService.addTrainingHours("john", LocalDate.of(2026, 7, 10), 45);

        ArgumentCaptor<MonthlySummery> captor = ArgumentCaptor.forClass(MonthlySummery.class);
        verify(monthlySummeryRepository).save(captor.capture());
        assertEquals(45, captor.getValue().getDuration());
    }

    @Test
    void testAddTrainingHoursThrowsUserNotFoundExceptionWhenTrainerNotFound() {
        when(trainerWorkloadRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainerService.addTrainingHours("john", LocalDate.of(2026, 7, 10), 30));
    }

    @Test
    void testDeleteTrainingHoursSubtractsFromExistingSummary() {
        TrainerWorkload workload = new TrainerWorkload(1L, "john", "John", "Doe", true, Collections.emptyList());
        MonthlySummery summary = new MonthlySummery(1L, LocalDate.of(2026, 7, 1), 60, workload);

        when(trainerWorkloadRepository.findByUsername("john")).thenReturn(Optional.of(workload));
        when(monthlySummeryRepository.findByIdAndDate(eq(1L), eq(LocalDate.of(2026, 7, 1))))
                .thenReturn(Optional.of(summary));

        trainerService.deleteTrainingHours("john", LocalDate.of(2026, 7, 10), 20);

        ArgumentCaptor<MonthlySummery> captor = ArgumentCaptor.forClass(MonthlySummery.class);
        verify(monthlySummeryRepository).save(captor.capture());
        assertEquals(40, captor.getValue().getDuration());
    }

    @Test
    void testDeleteTrainingHoursDeletesSummaryWhenDurationBecomesZero() {
        TrainerWorkload workload = new TrainerWorkload(1L, "john", "John", "Doe", true, Collections.emptyList());
        MonthlySummery summary = new MonthlySummery(1L, LocalDate.of(2026, 7, 1), 30, workload);

        when(trainerWorkloadRepository.findByUsername("john")).thenReturn(Optional.of(workload));
        when(monthlySummeryRepository.findByIdAndDate(eq(1L), eq(LocalDate.of(2026, 7, 1))))
                .thenReturn(Optional.of(summary));

        trainerService.deleteTrainingHours("john", LocalDate.of(2026, 7, 10), 30);

        verify(monthlySummeryRepository).delete(summary);
        verify(monthlySummeryRepository, never()).save(any());
    }

    @Test
    void testDeleteTrainingHoursThrowsNegativeDurationExceptionWhenDurationBecomesNegative() {
        TrainerWorkload workload = new TrainerWorkload(1L, "john", "John", "Doe", true, Collections.emptyList());
        MonthlySummery summary = new MonthlySummery(1L, LocalDate.of(2026, 7, 1), 10, workload);

        when(trainerWorkloadRepository.findByUsername("john")).thenReturn(Optional.of(workload));
        when(monthlySummeryRepository.findByIdAndDate(eq(1L), eq(LocalDate.of(2026, 7, 1))))
                .thenReturn(Optional.of(summary));

        assertThrows(NegativeDurationException.class,
                () -> trainerService.deleteTrainingHours("john", LocalDate.of(2026, 7, 10), 30));
    }

    @Test
    void testDeleteTrainingHoursThrowsUserNotFoundExceptionWhenTrainerNotFound() {
        when(trainerWorkloadRepository.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainerService.deleteTrainingHours("john", LocalDate.of(2026, 7, 10), 30));
    }
}
