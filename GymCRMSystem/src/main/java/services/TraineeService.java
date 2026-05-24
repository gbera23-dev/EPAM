package services;

import entities.Trainee;
import entities.Trainer;
import entities.Training;
import entities.TrainingType;

import java.time.LocalDate;
import java.util.List;

public interface TraineeService {

    void createTraineeProfile(Trainee trainee);

    Trainee selectTraineeProfileById(long traineeId);

    Trainee selectTraineeProfileByUsername(String username);

    void updateTraineeProfile(Trainee trainee);

    void updateTraineeListOfTrainers(long traineeId,
                                     List<String> trainerUsernames);

    void deleteTraineeProfileById(long traineeId);

    void deleteTraineeProfileByUsername(String username);

    void changeTraineeProfilePassword(String username,
                                      String oldPassword, String newPassword);

    void activateTraineeProfile(long traineeId);

    void deactivateTraineeProfile(long traineeId);

    List<Training> getTrainingsForTrainee(String username,
                                LocalDate fromDate,
                                LocalDate toDate,
                                String trainerName,
                                String trainingTypeName);

    List<Trainer> getTrainersNotAssignedToTrainee(String username);


    boolean validateTraineeProfile(String username, String password);

}