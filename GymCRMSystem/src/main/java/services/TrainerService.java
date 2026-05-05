package services;

import Utils.StringUtils;
import entities.Trainee;
import entities.Trainer;
import entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import persistence.TrainerDAO;

import java.util.List;

@Service
public class TrainerService {

    @Autowired
    TrainerDAO trainerDAO;

    public void createTrainerProfile(Trainer trainer) {
        User user = trainer.getUser();
        String username = generateUsername(trainer);
        String password = StringUtils.generateRandomPassword();

        user.setUserName(username);
        user.setPassword(password);


        trainerDAO.save(trainer.getTrainerPK(), trainer);
    }

    public void updateTrainerProfile(Trainer trainer) {
        trainerDAO.save(trainer.getTrainerPK(), trainer);
    }

    public Trainer selectTrainerProfile(long trainerId) {
        return trainerDAO.getEntity(trainerId);
    }

    private String generateUsername(Trainer trainer) {
        User user = trainer.getUser();
        List<Trainer> trainers = trainerDAO.getAll();
        String username = user.getFirstName() + "." + user.getLastName();
        long count = trainers.stream().filter(t -> t.getUser().getUserName().startsWith(username))
                .count();
        return count != 0 ? username + count : username;
    }

}
