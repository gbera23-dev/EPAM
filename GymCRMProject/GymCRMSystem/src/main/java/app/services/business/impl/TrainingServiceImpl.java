package app.services.business.impl;

import app.aop.annotations.ServiceLayer;
import app.domain.entities.Trainee;
import app.domain.entities.Trainer;
import app.domain.entities.Training;
import app.domain.entities.TrainingType;
import app.domain.exceptions.TrainingNotFoundException;
import app.domain.exceptions.UserNotFoundException;
import app.services.business.interfaces.TrainingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.domain.persistence.TraineeRepository;
import app.domain.persistence.TrainerRepository;
import app.domain.persistence.TrainingRepository;

import java.time.LocalDate;

@Service
@ServiceLayer
public class TrainingServiceImpl implements TrainingService {

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
        return trainingRepository.findById(trainingId).orElseThrow(() ->
                new TrainingNotFoundException("Such training " +
                "does not exist!"));
    }

    @Override
    @Transactional
    public void addTraining(String traineeUsername, String trainerUsername,
                            String trainingName, LocalDate date, int duration) {
        Training training = new Training();

        Trainee trainee = traineeRepository.findByUserUsername(traineeUsername)
                .orElseThrow(
                        () -> new UserNotFoundException("Could not find user with username!")
                );

        Trainer trainer = trainerRepository.findByUserUsername(trainerUsername)
                .orElseThrow(
                        () -> new UserNotFoundException("Could not find user with username!")
                );;


        TrainingType trainingType = trainer.getTrainingType();

        training.setName(trainingName);
        training.setDate(date);
        training.setDuration(duration);
        training.setTrainingType(trainingType);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        trainingRepository.save(training);
    }

    @Override
    @Transactional
    public void deleteTraining(long trainingId) {
        Training training = trainingRepository.findById(trainingId)
                        .orElseThrow(
                                () -> new TrainingNotFoundException("Such training does not exist!")
                        );

        trainingRepository.delete(training);
    }

}
