package services;

import entities.Trainee;
import entities.User;
import org.springframework.stereotype.Service;
import persistence.TraineeDAO;
import utils.UserUtils;

import java.util.List;


@Service
public class TraineeServiceImpl implements TraineeService {

    TraineeDAO traineeDAO;

    public TraineeServiceImpl(TraineeDAO traineeDAO) {
        this.traineeDAO = traineeDAO;
    }

    public void createTraineeProfile(Trainee trainee) {
        User currentUser = trainee.getUser();
        List<User> users = traineeDAO.getAll().
                stream().
                map(Trainee::getUser).
                toList();
        UserUtils.generateUserCredentials(currentUser, users);

        traineeDAO.save(trainee.getTraineePk(), trainee);
    }

    public Trainee selectTraineeProfile(long traineeId) {
        return traineeDAO.getEntity(traineeId);
    }

    public void updateTraineeProfile(Trainee trainee) {
        traineeDAO.save(trainee.getTraineePk(), trainee);
    }

    public void deleteTraineeProfile(long traineeId) {
        traineeDAO.delete(traineeId);
    }

}
