package services;

import entities.Trainee;

public interface TraineeService {

    void createTraineeProfile(Trainee trainee);

    Trainee selectTraineeProfile(long traineeId);

    void updateTraineeProfile(Trainee trainee);

    void deleteTraineeProfile(long traineeId);

}