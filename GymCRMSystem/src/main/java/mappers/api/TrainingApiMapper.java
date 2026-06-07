package mappers.api;

import dto.api.response.TrainingResponse;
import dto.api.response.TrainingTypeResponse;
import entities.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingApiMapper {

    public TrainingResponse toTrainingResponse(Training training) {
        return new TrainingResponse(
                training.getName(),
                training.getDate(),
                new TrainingTypeResponse(training.getTrainingType().getName(), training.getTrainingType().getId()),
                training.getDuration(),
                training.getTrainer().getUser().getFirstName() + " " +
                        training.getTrainer().getUser().getLastName()
        );
    }


}
