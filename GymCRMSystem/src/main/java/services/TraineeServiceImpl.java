package services;

import entities.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import persistence.TraineeRepository;
import persistence.TrainerRepository;
import persistence.TrainingRepository;
import utils.UserUtils;

import java.time.LocalDate;
import java.util.List;


@Service
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;

    public TraineeServiceImpl(TraineeRepository traineeRepository,
                              TrainerRepository trainerRepository,
                              TrainingRepository trainingRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
    }

    @Override
    @Transactional
    public void createTraineeProfile(Trainee trainee) {
        User currentUser = trainee.getUser();

        List<User> users = traineeRepository.getUsernameWithMaxNumberSuffix(trainee);

        UserUtils.generateUserCredentials(currentUser, users);

        traineeRepository.save(trainee);
    }

    @Override
    public Trainee selectTraineeProfileById(long traineeId) {
        return traineeRepository.findById(traineeId).orElseThrow(() ->
                new EntityNotFoundException("Could not find Trainee!")
        );
    }

    @Override
    public Trainee selectTraineeProfileByUsername(String username) {
        return traineeRepository.findByUserUsername(username);
    }

    @Override
    @Transactional
    public void updateTraineeProfile(Trainee trainee) {
        traineeRepository.save(trainee);
    }

    @Override
    @Transactional
    public void updateTraineeListOfTrainers(long traineeId, List<String> trainerUsernames) {
        Trainee trainee = traineeRepository.findById(traineeId).orElseThrow(() -> new
                IllegalArgumentException("Trainee not found"));

        List<Trainer> trainers =  trainerRepository.findByUserUsernameIn(trainerUsernames);

        trainee.getTrainers().clear();
        trainee.getTrainers().addAll(trainers);
    }

    @Override
    @Transactional
    public void deleteTraineeProfileById(long traineeId) {
        Trainee trainee = traineeRepository.findById(traineeId)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));

        for (Trainer trainer : trainee.getTrainers()) {
            trainer.getTrainees().remove(trainee);
        }

        if(trainee.getTrainings() != null)
            trainee.getTrainings().clear();

        trainee.getUser().setTrainee(null);

        traineeRepository.delete(trainee);
    }

    @Override
    @Transactional
    public void deleteTraineeProfileByUsername(String username) {
        Trainee trainee = traineeRepository.findByUserUsername(username);

        if (trainee == null) {
            throw new EntityNotFoundException("Trainee not found with username: " + username);
        }

        for (Trainer trainer : trainee.getTrainers()) {
            trainer.getTrainees().remove(trainee);
        }

        if(trainee.getTrainings() != null)
            trainee.getTrainings().clear();

        trainee.getUser().setTrainee(null);

        traineeRepository.delete(trainee);
    }

    @Override
    @Transactional
    public void activateTraineeProfile(long traineeId) {
        Trainee trainee = traineeRepository.findById(traineeId).orElseThrow(() ->
                 new EntityNotFoundException("Trainee not found!"));

        if (trainee.getUser().isActive()) {
            throw new IllegalStateException("Trainee profile is already active!");
        }

        trainee.getUser().setActive(true);
    }

    @Override
    @Transactional
    public void deactivateTraineeProfile(long traineeId) {
        Trainee trainee = traineeRepository.findById(traineeId).orElseThrow(() ->
                new EntityNotFoundException("Trainee not found!"));

        if (!trainee.getUser().isActive()) {
            throw new IllegalStateException("Trainee profile is already inactive!");
        }

        trainee.getUser().setActive(false);
    }

    @Override
    public List<Training> getTrainingsForTrainee(String username, LocalDate fromDate,
                                                 LocalDate toDate, String trainerName, String trainingTypeName) {

        return trainingRepository.findTrainingsByTraineeCriteria(username, fromDate, toDate, trainerName,
                trainingTypeName);
    }

    @Override
    public List<Trainer> getTrainersNotAssignedToTrainee(String username) {
        return trainerRepository.findTrainersNotAssignedToTrainee(username);
    }
}
