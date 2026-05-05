package services;

import Utils.StringUtils;
import entities.Trainee;
import entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import persistence.TraineeDAO;

import java.util.List;



@Service
public class TraineeService {

    @Autowired
    TraineeDAO traineeDAO;

    public void createTraineeProfile(Trainee trainee) {
        User user = trainee.getUser();
        String username = generateUsername(trainee);
        String password = StringUtils.generateRandomPassword();

        user.setUserName(username);
        user.setPassword(password);

        traineeDAO.save(trainee.getTraineePK(), trainee);
    }

    public Trainee selectTraineeProfile(long traineeId) {
        return traineeDAO.getEntity(traineeId);
    }

    public void updateTraineeProfile(Trainee trainee) {
        traineeDAO.save(trainee.getTraineePK(), trainee);
    }

    public void deleteTraineeProfile(long traineeId) {
        traineeDAO.delete(traineeId);
    }


    private String generateUsername(Trainee trainee) {
        User user = trainee.getUser();
        List<Trainee> trainees = traineeDAO.getAll();
        String username = user.getFirstName() + "." + user.getLastName();
        long count = trainees.stream().filter(t -> t.getUser().getUserName().startsWith(username))
                .count();
        return count != 0 ? username + count : username;
    }
}
