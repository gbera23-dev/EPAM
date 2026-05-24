package services;

import entities.Trainee;
import entities.Trainer;
import entities.Training;
import entities.User;
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
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;

    public TrainerServiceImpl(TrainerRepository trainerRepository,
                              TrainingRepository trainingRepository) {
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
    }


    @Override
    @Transactional
    public void createTrainerProfile(Trainer trainer) {
        User currentUser = trainer.getUser();

        List<User> users = trainerRepository.getUsernameWithMaxNumberSuffix(trainer);

        UserUtils.generateUserCredentials(currentUser, users);

        trainerRepository.save(trainer);
    }

    @Override
    @Transactional
    public void updateTrainerProfile(Trainer trainer) {
        trainerRepository.save(trainer);
    }

    @Override
    public Trainer selectTrainerProfileById(long trainerId) {
        return trainerRepository.getReferenceById(trainerId);
    }

    @Override
    public Trainer selectTrainerProfileByUsername(String username) {
        return trainerRepository.findByUserUsername(username);
    }

    @Override
    @Transactional
    public void changeTrainerProfilePassword(String username, String oldPassword, String newPassword) {
        if(!validateTrainerProfile(username, oldPassword))
            throw new IllegalArgumentException("Username or password is incorrect!");

        Trainer trainer = trainerRepository.findByUserUsername(username);

        trainer.getUser().setPassword(newPassword);
    }

    @Override
    @Transactional
    public void activateTrainerProfile(long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(() ->
                new EntityNotFoundException("Trainer not found!"));

        trainer.getUser().setActive(true);
    }

    @Override
    @Transactional
    public void deactivateTrainerProfile(long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(() ->
                new EntityNotFoundException("Trainer not found!"));

        trainer.getUser().setActive(false);
    }

    @Override
    public List<Training> getTrainingsForTrainer(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        return trainingRepository.findTrainingsByTrainerCriteria(username, fromDate, toDate, traineeName);
    }

    @Override
    public boolean validateTrainerProfile(String username, String password) {
        Trainer trainer = trainerRepository.findByUserUsername(username);

        return trainer != null && trainer.getUser().getPassword().equals(password);
    }
}
