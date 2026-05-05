package facade;

import entities.Trainee;
import entities.Trainer;
import entities.Training;
import org.springframework.stereotype.Component;
import services.TraineeService;
import services.TrainerService;
import services.TrainingService;

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
    public void deleteTrainee(Long id) {
        traineeService.deleteTraineeProfile(id);
    }

    public Trainee getTrainee(Long id) {
        return traineeService.selectTraineeProfile(id);
    }

    public void createTrainer(Trainer trainer) {
        trainerService.createTrainerProfile(trainer);
    }

    public void updateTrainer(Trainer trainer) {
        trainerService.updateTrainerProfile(trainer);
    }

    public Trainer getTrainer(Long id) {
        return trainerService.selectTrainerProfile(id);
    }

    public void addTraining(Training training) {
        trainingService.createTrainingProfile(training);
    }

    public Training getTraining(Long id) {
        return trainingService.selectTrainingProfile(id);
    }


}
