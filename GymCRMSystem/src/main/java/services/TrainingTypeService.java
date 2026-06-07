package services;

import entities.TrainingType;

import java.util.List;

public interface TrainingTypeService {

    List<TrainingType> getTrainingTypes();

    TrainingType getTrainingTypeById(long id);
}
