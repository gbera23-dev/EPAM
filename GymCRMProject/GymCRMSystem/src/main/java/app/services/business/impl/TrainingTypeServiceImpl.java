package app.services.business.impl;

import app.aop.annotations.ServiceLayer;
import app.domain.entities.TrainingType;
import app.domain.exceptions.TrainingTypeNotFoundException;
import app.services.business.interfaces.TrainingTypeService;
import org.springframework.stereotype.Service;
import app.domain.persistence.TrainingTypeRepository;

import java.util.List;

@Service
@ServiceLayer
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
        return trainingTypeRepository.findById(id)
                .orElseThrow(
                        () -> new TrainingTypeNotFoundException("Could not find training type!")
                );
    }

}
