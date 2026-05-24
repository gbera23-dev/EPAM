package facade;

import entities.Trainee;
import entities.Trainer;
import entities.Training;
import org.springframework.stereotype.Component;
import services.TraineeService;
import services.TrainerService;
import services.TrainingService;

import java.time.LocalDate;
import java.util.List;

@Component
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.trainingService = trainingService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    public void createTrainee(Trainee trainee) {
        traineeService.createTraineeProfile(trainee);
    }

    public void updateTrainee(Trainee trainee) {
        traineeService.updateTraineeProfile(trainee);
    }

    public void deleteTraineeById(long traineeId) {
        traineeService.deleteTraineeProfileById(traineeId);
    }

    public void deleteTraineeByUsername(String username) {
        traineeService.deleteTraineeProfileByUsername(username);
    }

    public Trainee getTraineeById(long traineeId) {
        return traineeService.selectTraineeProfileById(traineeId);
    }

    public Trainee getTraineeByUsername(String username) {
        return traineeService.selectTraineeProfileByUsername(username);
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        traineeService.changeTraineeProfilePassword(username, oldPassword, newPassword);
    }

    public void activateTrainee(long traineeId) {
        traineeService.activateTraineeProfile(traineeId);
    }

    public void deactivateTrainee(long traineeId) {
        traineeService.deactivateTraineeProfile(traineeId);
    }

    public List<Training> getTrainingsForTrainee(String username, LocalDate fromDate, LocalDate toDate,
                                                 String trainerName, String trainingTypeName) {
        return traineeService.getTrainingsForTrainee(username, fromDate, toDate, trainerName, trainingTypeName);
    }

    public List<Trainer> getTrainersNotAssignedToTrainee(String username) {
        return traineeService.getTrainersNotAssignedToTrainee(username);
    }

    public void updateTraineeListOfTrainers(long traineeId, List<String> trainerUsernames) {
        traineeService.updateTraineeListOfTrainers(traineeId, trainerUsernames);
    }

    public boolean validateTrainee(String username, String password) {
        return traineeService.validateTraineeProfile(username, password);
    }

    public void createTrainer(Trainer trainer) {
        trainerService.createTrainerProfile(trainer);
    }

    public void updateTrainer(Trainer trainer) {
        trainerService.updateTrainerProfile(trainer);
    }

    public Trainer getTrainerById(long trainerId) {
        return trainerService.selectTrainerProfileById(trainerId);
    }

    public Trainer getTrainerByUsername(String username) {
        return trainerService.selectTrainerProfileByUsername(username);
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        trainerService.changeTrainerProfilePassword(username, oldPassword, newPassword);
    }

    public void activateTrainer(long trainerId) {
        trainerService.activateTrainerProfile(trainerId);
    }

    public void deactivateTrainer(long trainerId) {
        trainerService.deactivateTrainerProfile(trainerId);
    }

    public List<Training> getTrainingsForTrainer(String username, LocalDate fromDate, LocalDate toDate,
                                                 String traineeName) {
        return trainerService.getTrainingsForTrainer(username, fromDate, toDate, traineeName);
    }

    public boolean validateTrainer(String username, String password) {
        return trainerService.validateTrainerProfile(username, password);
    }

    public Training getTraining(long trainingId) {
        return trainingService.selectTraining(trainingId);
    }

    public void addTraining(String traineeUsername, String trainerUsername,
                            String trainingName, LocalDate date, int duration) {
        trainingService.addTraining(traineeUsername, trainerUsername, trainingName, date, duration);
    }
}
