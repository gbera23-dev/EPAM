package services;

import entities.Trainer;
import entities.User;
import org.springframework.stereotype.Service;
import persistence.TrainerRepository;
import utils.UserUtils;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;

    public TrainerServiceImpl(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    public void createTrainerProfile(Trainer trainer) {
        User currentUser = trainer.getUser();

        List<User> users = trainerRepository.findAll().
                        stream().
                        map(Trainer::getUser).
                        toList();


        UserUtils.generateUserCredentials(currentUser, users);

        trainerRepository.save(trainer);
    }

    public void updateTrainerProfile(Trainer trainer) {
        trainerRepository.save(trainer);
    }

    public Trainer selectTrainerProfile(long trainerId) {
        return trainerRepository.findById(trainerId).orElse(null);
    }


}
