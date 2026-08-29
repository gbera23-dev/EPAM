package app.api.mappers;

import app.api.dto.response.TrainingResponse;
import app.api.dto.response.TrainingTypeResponse;
import app.domain.entities.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingApiMapper {

    public TrainingResponse toTrainingResponse(Training training) {
        return new TrainingResponse(
                training.getId(),
                training.getName(),
                training.getDate(),
                new TrainingTypeResponse(training.getTrainingType().getName(), training.getTrainingType().getId()),
                training.getDuration(),
                training.getTrainer().getUser().getFirstName() + " " +
                        training.getTrainer().getUser().getLastName()
        );
    }


}
