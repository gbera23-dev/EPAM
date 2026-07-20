package app.mappers.api;

import app.dto.api.response.TrainingTypeResponse;
import app.entities.TrainingType;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypeApiMapper {


    public TrainingTypeResponse toTrainingTypeResponse(TrainingType trainingType) {
        return new TrainingTypeResponse(
                trainingType.getName(),
                trainingType.getId()
        );
    }

}
