package services;

import entities.Trainee;
import entities.Trainer;
import entities.Training;
import entities.TrainingType;

import java.time.LocalDate;
import java.util.List;

public interface TrainerService {

    void createTrainerProfile(Trainer trainer);

    void updateTrainerProfile(Trainer trainer);

    Trainer selectTrainerProfileById(long trainerId);

    Trainer selectTrainerProfileByUsername(String username);

    void changeTrainerProfilePassword(String username,
                                      String oldPassword,
                                      String newPassword);

    void activateTrainerProfile(long trainerId);

    void deactivateTrainerProfile(long trainerId);

    List<Training> getTrainingsForTrainer(String username,
                                LocalDate fromDate,
                                LocalDate toDate,
                                String traineeName);

    boolean validateTrainerProfile(String username, String password);

}
