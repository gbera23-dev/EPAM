package com.example.Trainer_history_service.services;

import java.time.LocalDate;

/**
 * Service interface for managing trainer workloads and training hours.
 * <p>
 * Provides operations to create trainer workload profiles and track
 * training hours per date.
 * </p>
 */
public interface TrainerService {

    /**
     * Creates a new workload profile for a trainer.
     * <p>
     * Should be called when a trainer is encountered for the first time
     * and no workload record exists yet.
     * </p>
     *
     * @param username  the unique username of the trainer
     * @param firstName the trainer's first name
     * @param lastName  the trainer's last name
     * @param isActive  whether the trainer is currently active
     */
    void createNewWorkload(String username, String firstName, String lastName,
                           boolean isActive);

    /**
     * Checks whether a workload record exists for the given trainer.
     *
     * @param username the unique username of the trainer
     * @return {@code true} if a workload record exists, {@code false} otherwise
     */
    boolean workloadExists(String username);

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
     * Adds training hours to a trainer's workload for the specified date.
     * <p>
     * If an entry for the given date already exists, the hours are accumulated.
     * </p>
     *
     * @param username the unique username of the trainer
     * @param date     the date on which the training took place
     * @param hours    the number of hours to add (must be positive)
     */
    void addTrainingHours(String username, LocalDate date, int hours);

    /**
     * Removes training hours from a trainer's workload for the specified date.
     * <p>
     * If the resulting value would be negative, implementations may either
     * set the total to zero or throw an exception, depending on business rules.
     * </p>
     *
     * @param username the unique username of the trainer
     * @param date     the date from which hours should be removed
     * @param hours    the number of hours to remove (must be positive)
     */
    void deleteTrainingHours(String username, LocalDate date, int hours);
}