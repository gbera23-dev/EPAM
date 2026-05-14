package services;

import entities.Training;

public interface TrainingService {

    Training selectTrainingProfile(long trainingId);

    void createTrainingProfile(Training training);
}
