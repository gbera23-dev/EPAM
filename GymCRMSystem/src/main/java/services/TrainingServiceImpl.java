package services;

import entities.Training;
import org.springframework.stereotype.Service;
import persistence.TrainingDAO;

@Service
public class TrainingServiceImpl implements TrainingService{

    private final TrainingDAO trainingDAO;

    public TrainingServiceImpl(TrainingDAO trainingDAO) {
        this.trainingDAO = trainingDAO;
    }

    public Training selectTrainingProfile(long trainingId) {
        return trainingDAO.getEntity(trainingId);
    }

    public void createTrainingProfile(Training training) {
        trainingDAO.save(training.getId(), training);
    }

}
