package app.services;

import app.entities.Trainee;
import app.entities.Trainer;
import app.entities.Training;
import app.entities.TrainingType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.persistence.TraineeRepository;
import app.persistence.TrainerRepository;
import app.persistence.TrainingRepository;

import java.time.LocalDate;

@Service
public class TrainingServiceImpl implements TrainingService{

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public TrainingServiceImpl(TrainingRepository trainingRepository,
                               TraineeRepository traineeRepository,
                               TrainerRepository trainerRepository) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    public Training selectTraining(long trainingId) {
        return trainingRepository.findById(trainingId).orElseThrow(() -> new EntityNotFoundException("Such training " +
                "does not exist!"));
    }

    @Override
    @Transactional
    public void addTraining(String traineeUsername, String trainerUsername,
                            String trainingName, LocalDate date, int duration) {
        Training training = new Training();

        Trainee trainee = traineeRepository.findByUserUsername(traineeUsername);

        Trainer trainer = trainerRepository.findByUserUsername(trainerUsername);

        if(trainee == null || trainer == null) {
            throw new EntityNotFoundException("Could not find either trainee or trainer in database!");
        }
        TrainingType trainingType = trainer.getTrainingType();

        training.setName(trainingName);
        training.setDate(date);
        training.setDuration(duration);
        training.setTrainingType(trainingType);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        trainingRepository.save(training);
    }
}
