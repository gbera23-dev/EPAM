package app.services;

import app.entities.TrainingType;
import org.springframework.stereotype.Service;
import app.persistence.TrainingTypeRepository;

import java.util.List;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {


    private final TrainingTypeRepository trainingTypeRepository;

    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Override
    public List<TrainingType> getTrainingTypes() {
        return trainingTypeRepository.findAll();
    }

    @Override
    public TrainingType getTrainingTypeById(long id) {
        return trainingTypeRepository.findById(id);
    }

}
