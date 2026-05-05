package services;

import entities.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import persistence.TrainingDAO;

@Service
public class TrainingService {

    @Autowired
    TrainingDAO trainingDAO;

    public Training selectTrainingProfile(long trainingId) {
        return trainingDAO.getEntity(trainingId);
    }

    public void createTrainingProfile(Training training) {
        trainingDAO.save(training.getTrainingPK(), training);
    }

}
