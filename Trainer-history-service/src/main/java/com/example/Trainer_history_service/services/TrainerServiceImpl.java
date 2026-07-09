package com.example.Trainer_history_service.services;

import com.example.Trainer_history_service.entities.MonthlySummary;
import com.example.Trainer_history_service.entities.TrainerWorkload;
import com.example.Trainer_history_service.exceptions.MonthlySummaryNotFoundException;
import com.example.Trainer_history_service.exceptions.NegativeDurationException;
import com.example.Trainer_history_service.exceptions.UserNotFoundException;
import com.example.Trainer_history_service.repository.MonthlySummeryRepository;
import com.example.Trainer_history_service.repository.TrainerWorkloadRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;

@Service
@AllArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerWorkloadRepository trainerWorkloadRepository;
    private final MonthlySummeryRepository monthlySummeryRepository;

    @Override
    @Transactional
    public void createNewWorkload(String username, String firstName, String lastName, boolean isActive) {
        TrainerWorkload trainerWorkload = new TrainerWorkload
                (null, username, firstName, lastName, isActive, Collections.emptyList());

        trainerWorkloadRepository.save(trainerWorkload);
    }

    @Override
    public boolean workloadExists(String username) {
        return trainerWorkloadRepository.existsByUsername(username);
    }

    @Override
    public Integer getTrainingHours(String username, LocalDate date) {
        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("could not find trainer workload!"));

        MonthlySummary monthlySummary = monthlySummeryRepository.findByIdAndDate(trainerWorkload.getId(),
                        LocalDate.of(date.getYear(), date.getMonth(), 1))
                .orElseThrow(
                        () -> new MonthlySummaryNotFoundException("could not find monthly summary!")
                );

        return monthlySummary.getDuration();
    }

    @Override
    @Transactional
    public void addTrainingHours(String username, LocalDate date, int hours) {
        MonthlySummary monthlySummary = getMonthlySummary(username, date, hours);

        int durationToSet = monthlySummary.getDuration() + hours;

        monthlySummary.setDuration(durationToSet);
        monthlySummeryRepository.save(monthlySummary);
    }

    @Override
    @Transactional
    public void deleteTrainingHours(String username, LocalDate date, int hours) {

        MonthlySummary monthlySummary = getMonthlySummary(username, date, hours);

        int durationToSet = monthlySummary.getDuration() - hours;

        if(durationToSet == 0) {
            monthlySummeryRepository.delete(monthlySummary);
            return;
        }

        if(durationToSet < 0) {
            throw new NegativeDurationException("training hours cannot become negative!");
        }

        monthlySummary.setDuration(durationToSet);
        monthlySummeryRepository.save(monthlySummary);
    }

    private MonthlySummary getMonthlySummary(String username, LocalDate date, int hours) {
        if(hours <= 0) {
            throw new NegativeDurationException("Number of hours cannot be negative!");
        }

        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("could not find trainer workload!"));


        return monthlySummeryRepository.findByIdAndDate(trainerWorkload.getId(),
                        LocalDate.of(date.getYear(), date.getMonth(), 1))
                .orElse(new MonthlySummary(null,
                        LocalDate.of(date.getYear(), date.getMonth(), 1), 0, trainerWorkload)
                );
    }

}
