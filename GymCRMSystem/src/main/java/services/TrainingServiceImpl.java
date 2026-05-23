package services;

import entities.Training;
import org.springframework.stereotype.Service;
import persistence.TrainingRepository;

@Service
public class TrainingServiceImpl implements TrainingService{

    private final TrainingRepository trainingRepository;

    public TrainingServiceImpl(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    public Training selectTrainingProfile(long trainingId) {
        return trainingRepository.getReferenceById(trainingId);
    }

    public void createTrainingProfile(Training training) {
        trainingRepository.save(training);
    }

}
