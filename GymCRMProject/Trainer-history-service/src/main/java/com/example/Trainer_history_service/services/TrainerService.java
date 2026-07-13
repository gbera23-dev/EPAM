package com.example.Trainer_history_service.services;

import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.entities.TrainerWorkload;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing trainer workloads and training hours.
 * <p>
 * Provides operations to create trainer workload profiles and track
 * training hours per date.
 * </p>
 */
public interface TrainerService {

    /**
     * Retrieves the total training hours logged for a trainer on a specific date.
     *
     * @param username the unique username of the trainer
     * @param date     the date for which training hours are queried
     * @return the number of training hours recorded on that date,
     *         or {@code null} if no entry exists
     */
    Integer getTrainingHours(String username, LocalDate date);

    /**
     * Updates Training hours of multiple Trainers at once
     * @param trainerWorkloadRequests list of requests, where each contain data about how many hours
     *                                should be removed or added to trainer at particular date
     */
    void updateTrainingHoursInBatch(List<TrainerWorkloadRequest> trainerWorkloadRequests);

    /**
     * Updates Training hours of a single trainer
     * @param trainerWorkloadRequest a request, which contains data about how many hours should be removed or added
     *                               to trainer at particular date
     */
    void updateTrainingHours(TrainerWorkloadRequest trainerWorkloadRequest);

}