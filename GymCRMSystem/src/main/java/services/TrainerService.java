package services;

import entities.Trainer;
import entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import persistence.TrainerDAO;
import utils.UserUtils;

import java.util.List;

@Service
public class TrainerService {

    @Autowired
    TrainerDAO trainerDAO;

    public void createTrainerProfile(Trainer trainer) {
        User currentUser = trainer.getUser();
        List<User> users = trainerDAO.getAll().
                        stream().
                        map(Trainer::getUser).
                        toList();
        UserUtils.generateUserCredentials(currentUser, users);

        trainerDAO.save(trainer.getTrainerPK(), trainer);
    }

    public void updateTrainerProfile(Trainer trainer) {
        trainerDAO.save(trainer.getTrainerPK(), trainer);
    }

    public Trainer selectTrainerProfile(long trainerId) {
        return trainerDAO.getEntity(trainerId);
    }


}
