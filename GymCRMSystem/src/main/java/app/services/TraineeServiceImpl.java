package app.services;

import app.entities.*;
import app.exceptions.UserNotFoundException;
import app.persistence.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.persistence.TraineeRepository;
import app.persistence.TrainerRepository;
import app.persistence.TrainingRepository;
import app.utils.UserUtils;

import java.time.LocalDate;
import java.util.List;


@Service
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TraineeServiceImpl(TraineeRepository traineeRepository,
                              TrainerRepository trainerRepository,
                              TrainingRepository trainingRepository,
                              UserRepository userRepository,
                              PasswordEncoder passwordEncoder) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public String createTraineeProfile(Trainee trainee) {
        User currentUser = trainee.getUser();

        List<User> users = userRepository.findUsersByFirstNameAndLastName(trainee.getUser().getFirstName(),
                trainee.getUser().getLastName());

        UserUtils.generateUserCredentials(currentUser, users);

        String rawPassword = currentUser.getPassword();

        currentUser.setPassword(passwordEncoder.encode(currentUser.getPassword()));

        traineeRepository.save(trainee);

        return rawPassword;
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
        Trainee managedTrainee = traineeRepository.findById(traineeId)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found"));

        List<Trainer> incomingTrainers = trainerRepository.findByUserUsernameIn(trainerUsernames);

        for (Trainer oldTrainer : managedTrainee.getTrainers()) {
            oldTrainer.getTrainees().remove(managedTrainee);
        }
        managedTrainee.getTrainers().clear();

        for (Trainer newTrainer : incomingTrainers) {
            newTrainer.getTrainees().add(managedTrainee);
            managedTrainee.getTrainers().add(newTrainer);
        }

        traineeRepository.saveAndFlush(managedTrainee);
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

    @Override
    public List<Trainer> getTrainersAssignedToTrainee(String username) {
        return trainerRepository.findTrainersAssignedToTrainee(username);
    }
}
