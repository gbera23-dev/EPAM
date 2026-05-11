package services;

import entities.Trainee;
import entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import persistence.TraineeDAO;
import utils.UserUtils;

import java.util.List;


@Service
public class TraineeService {

    @Autowired
    TraineeDAO traineeDAO;



    public void createTraineeProfile(Trainee trainee) {
        User currentUser = trainee.getUser();
        List<User> users = traineeDAO.getAll().
                stream().
                map(Trainee::getUser).
                toList();
        UserUtils.generateUserCredentials(currentUser, users);

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

}
