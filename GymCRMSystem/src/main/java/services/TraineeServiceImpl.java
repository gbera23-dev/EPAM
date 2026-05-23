package services;

import entities.Trainee;
import entities.User;
import org.springframework.stereotype.Service;
import persistence.TraineeRepository;
import utils.UserUtils;

import java.util.List;


@Service
public class TraineeServiceImpl implements TraineeService {

    private final TraineeRepository traineeRepository;

    public TraineeServiceImpl(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    public void createTraineeProfile(Trainee trainee) {
        User currentUser = trainee.getUser();
        List<User> users = traineeRepository.findAll().
                stream().
                map(Trainee::getUser).
                toList();
        UserUtils.generateUserCredentials(currentUser, users);

        traineeRepository.save(trainee);
    }

    public Trainee selectTraineeProfile(long traineeId) {
        return traineeRepository.getReferenceById(traineeId);
    }

    public void updateTraineeProfile(Trainee trainee) {
        traineeRepository.save(trainee);
    }

    public void deleteTraineeProfile(long traineeId) {
        traineeRepository.deleteById(traineeId);
    }

}
