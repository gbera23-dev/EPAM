package services;

import entities.Training;
import entities.TrainingType;

import java.time.LocalDate;

public interface TrainingService {

    Training selectTraining(long trainingId);

    void addTraining(String traineeUsername,
                     String trainerUsername,
                     String trainingName,
                     LocalDate date,
                     int duration);
}
