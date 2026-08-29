package app.services.business.impl;

import app.aop.annotations.ServiceLayer;
import app.domain.entities.Trainer;
import app.domain.entities.Training;
import app.domain.entities.User;
import app.domain.exceptions.UserAlreadyActiveException;
import app.domain.exceptions.UserAlreadyInactiveException;
import app.domain.exceptions.UserNotFoundException;
import app.domain.persistence.UserRepository;
import app.services.business.interfaces.TrainerService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.domain.persistence.TrainerRepository;
import app.domain.persistence.TrainingRepository;
import app.infrastructure.utils.UserUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@ServiceLayer
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TrainerServiceImpl(TrainerRepository trainerRepository,
                              TrainingRepository trainingRepository,
                              UserRepository userRepository,
                              PasswordEncoder passwordEncoder) {
        this.trainerRepository = trainerRepository;
        this.trainingRepository = trainingRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    @Transactional
    public String createTrainerProfile(Trainer trainer) {
        User currentUser = trainer.getUser();

        List<User> users = userRepository.findUsersByFirstNameAndLastName(trainer.getUser().getFirstName(),
                trainer.getUser().getLastName());

        UserUtils.generateUserCredentials(currentUser, users);

        String rawPassword = currentUser.getPassword();

        currentUser.setPassword(passwordEncoder.encode(currentUser.getPassword()));

        trainerRepository.save(trainer);

        return rawPassword;
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
        return trainerRepository.findByUserUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException("Could not find user with username!")
                );
    }

    @Override
    @Transactional
    public void activateTrainerProfile(long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(() ->
                new UserNotFoundException("Trainer not found!"));

        if (trainer.getUser().isActive()) {
            throw new UserAlreadyActiveException("Trainee profile is already active!");
        }

        trainer.getUser().setActive(true);
    }

    @Override
    @Transactional
    public void deactivateTrainerProfile(long trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId).orElseThrow(() ->
                new UserNotFoundException("Trainer not found!"));

        if (!trainer.getUser().isActive()) {
            throw new UserAlreadyInactiveException("Trainee profile is already inactive!");
        }

        trainer.getUser().setActive(false);
    }

    @Override
    public List<Training> getTrainingsForTrainer(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        return trainingRepository.findTrainingsByTrainerCriteria(username, fromDate, toDate, traineeName);
    }

}
