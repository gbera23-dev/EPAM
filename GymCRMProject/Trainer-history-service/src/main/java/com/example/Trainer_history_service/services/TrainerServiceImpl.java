package com.example.Trainer_history_service.services;

import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.entities.ActionType;
import com.example.Trainer_history_service.entities.MonthlySummary;
import com.example.Trainer_history_service.entities.TrainerWorkload;
import com.example.Trainer_history_service.exceptions.MonthlySummaryNotFoundException;
import com.example.Trainer_history_service.exceptions.NegativeDurationException;
import com.example.Trainer_history_service.exceptions.UserNotFoundException;
import com.example.Trainer_history_service.repository.MonthlySummaryRepository;
import com.example.Trainer_history_service.repository.TrainerWorkloadRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerWorkloadRepository trainerWorkloadRepository;
    private final MonthlySummaryRepository monthlySummaryRepository;

    @Override
    public Integer getTrainingHours(String username, LocalDate date) {
        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("could not find trainer workload!"));

        MonthlySummary monthlySummary = monthlySummaryRepository.findByTrainerWorkloadIdAndDate
                        (trainerWorkload.getId(),
                        LocalDate.of(date.getYear(), date.getMonth(), 1))
                .orElseThrow(
                        () -> new MonthlySummaryNotFoundException("could not find monthly summary!")
                );

        return monthlySummary.getDuration();
    }

    @Override
    @Transactional
    public void updateTrainingHoursInBatch(List<TrainerWorkloadRequest> trainerWorkloadsRequests) {

        trainerWorkloadsRequests = aggregateHours(trainerWorkloadsRequests);

        trainerWorkloadsRequests.forEach(
                twr -> {

                    if(!workloadExists(twr.getUsername())) {
                        createNewWorkload(twr.getUsername(), twr.getFirstName(), twr.getLastName(), twr.getIsActive());
                    }
                    MonthlySummary monthlySummary = determineMonthlySummary(twr);
                    if(monthlySummary != null) {
                        monthlySummaryRepository.save(monthlySummary);
                    }
                }
        );
    }

    @Override
    @Transactional
    public void updateTrainingHours(TrainerWorkloadRequest trainerWorkloadRequest) {
        if(!workloadExists(trainerWorkloadRequest.getUsername())) {
            createNewWorkload(trainerWorkloadRequest.getUsername(),
                    trainerWorkloadRequest.getFirstName(),
                    trainerWorkloadRequest.getLastName(),
                    trainerWorkloadRequest.getIsActive());
        }

        MonthlySummary resultingMonthlySummary = determineMonthlySummary(trainerWorkloadRequest);
        if(resultingMonthlySummary != null) {
            monthlySummaryRepository.save(resultingMonthlySummary);
        }
    }

    private MonthlySummary determineMonthlySummary(TrainerWorkloadRequest trainerWorkloadRequest) {
        ActionType actionType = trainerWorkloadRequest.getActionType();
        MonthlySummary resultingMonthlySummary;
        if(actionType.equals(ActionType.ADD)) {
            resultingMonthlySummary=addTrainingHours(trainerWorkloadRequest.getUsername(),
                    trainerWorkloadRequest.getTrainingDate(), trainerWorkloadRequest.getDuration());
        }
        else {
            resultingMonthlySummary=deleteTrainingHours(trainerWorkloadRequest.getUsername(),
                    trainerWorkloadRequest.getTrainingDate(), trainerWorkloadRequest.getDuration());
        }
        return resultingMonthlySummary;
    }


    private MonthlySummary addTrainingHours(String username, LocalDate date, int hours) {
        MonthlySummary monthlySummary = findOrCreateMonthlySummary(username, date, hours);

        int durationToSet = monthlySummary.getDuration() + hours;

        monthlySummary.setDuration(durationToSet);

        return monthlySummary;
    }

    private MonthlySummary deleteTrainingHours(String username, LocalDate date, int hours) {

        MonthlySummary monthlySummary = findOrCreateMonthlySummary(username, date, hours);

        int durationToSet = monthlySummary.getDuration() - hours;

        if(durationToSet == 0) {
            monthlySummaryRepository.delete(monthlySummary);
            return null;
        }

        if(durationToSet < 0) {
            throw new NegativeDurationException("training hours cannot become negative!");
        }

        monthlySummary.setDuration(durationToSet);
        return monthlySummary;
    }

    private MonthlySummary findOrCreateMonthlySummary(String username, LocalDate date, int hours) {
        if(hours < 0) {
            throw new NegativeDurationException("Number of hours cannot be negative!");
        }

        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("could not find trainer workload!"));


        return monthlySummaryRepository.
                findByTrainerWorkloadIdAndDate(trainerWorkload.getId(),
                LocalDate.of(date.getYear(), date.getMonth(), 1))
                .orElse(new MonthlySummary(null, LocalDate.of(date.getYear(), date.getMonth(), 1), 0,
                        trainerWorkload));
    }


    private void createNewWorkload(String username, String firstName, String lastName, boolean isActive) {
        TrainerWorkload trainerWorkload = new TrainerWorkload
                (null, username, firstName, lastName, isActive, Collections.emptyList());
        trainerWorkloadRepository.save(trainerWorkload);
    }

    private boolean workloadExists(String username) {
        return trainerWorkloadRepository.existsByUsername(username);
    }


    private List<TrainerWorkloadRequest> aggregateHours(List<TrainerWorkloadRequest> trainerWorkloadsRequests) {
        Map<String, TrainerWorkloadRequest> representativeRequests = trainerWorkloadsRequests.stream()
                .collect(Collectors.toMap(
                        t -> t.getUsername() + LocalDate.of(t.getTrainingDate().getYear(),
                                t.getTrainingDate().getMonth(), 1),
                        t -> t,
                        (existing, duplicate) -> existing
                ));

        Map<String, Integer> aggregateHours = trainerWorkloadsRequests.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getUsername() + LocalDate.of(t.getTrainingDate().getYear(),
                                t.getTrainingDate().getMonth(), 1),
                        Collectors.summingInt(t -> t.getActionType().equals(ActionType.ADD)
                                ? t.getDuration() : -t.getDuration())
                ));

        return aggregateHours.entrySet().stream()
                .map(entry -> {
                    TrainerWorkloadRequest r = representativeRequests.get(entry.getKey());
                    int total = entry.getValue();
                    r.setDuration(Math.abs(total));
                    r.setActionType(total >= 0 ? ActionType.ADD : ActionType.DELETE);
                    return r;
                })
                .toList();
    }
}
