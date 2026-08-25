package app.api.mappers;

import app.api.dto.response.TrainingTypeResponse;
import app.domain.entities.TrainingType;
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
