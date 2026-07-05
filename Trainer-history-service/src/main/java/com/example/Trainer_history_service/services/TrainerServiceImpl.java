package com.example.Trainer_history_service.services;

import com.example.Trainer_history_service.entities.MonthlySummery;
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

        MonthlySummery monthlySummery = monthlySummeryRepository.findByIdAndDate(trainerWorkload.getId(),
                        LocalDate.of(date.getYear(), date.getMonth(), 1))
                .orElseThrow(
                        () -> new MonthlySummaryNotFoundException("could not find monthly summary!")
                );

        return monthlySummery.getDuration();
    }

    @Override
    @Transactional
    public void addTrainingHours(String username, LocalDate date, int hours) {
        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("could not find trainer workload!"));

        MonthlySummery monthlySummery = monthlySummeryRepository.findByIdAndDate(trainerWorkload.getId(),
                        LocalDate.of(date.getYear(), date.getMonth(), 1))
                .orElse(new MonthlySummery(null,
                        LocalDate.of(date.getYear(), date.getMonth(), 1), 0, trainerWorkload)
                );

        int durationToSet = monthlySummery.getDuration() + hours;

        monthlySummery.setDuration(durationToSet);
        monthlySummeryRepository.save(monthlySummery);
    }

    @Override
    @Transactional
    public void deleteTrainingHours(String username, LocalDate date, int hours) {
        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("could not find trainer workload!"));


        MonthlySummery monthlySummery = monthlySummeryRepository.findByIdAndDate(trainerWorkload.getId(),
                        LocalDate.of(date.getYear(), date.getMonth(), 1))
                .orElse(new MonthlySummery(null,
                        LocalDate.of(date.getYear(), date.getMonth(), 1), 0, trainerWorkload)
                );

        int durationToSet = monthlySummery.getDuration() - hours;

        if(durationToSet == 0) {
            monthlySummeryRepository.delete(monthlySummery);
            return;
        }

        if(durationToSet < 0) {
            throw new NegativeDurationException("training hours cannot become negative!");
        }

        monthlySummery.setDuration(durationToSet);
        monthlySummeryRepository.save(monthlySummery);
    }
}
