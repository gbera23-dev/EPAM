package services;

import entities.Training;

import java.time.LocalDate;

/**
 * Operations for creating and retrieving {@link entities.Training} records.
 */
public interface TrainingService {

    /** @throws jakarta.persistence.EntityNotFoundException if not found */
    Training selectTraining(long trainingId);

    /**
     * Creates and persists a training session linking the given trainee and trainer.
     *
     * @throws jakarta.persistence.EntityNotFoundException if either username does not exist
     */
    void addTraining(String traineeUsername, String trainerUsername,
                     String trainingName, LocalDate date, int duration);
}