package services;

import entities.Trainer;

public interface TrainerService {

    void createTrainerProfile(Trainer trainer);

    void updateTrainerProfile(Trainer trainer);

    Trainer selectTrainerProfile(long trainerId);
}
