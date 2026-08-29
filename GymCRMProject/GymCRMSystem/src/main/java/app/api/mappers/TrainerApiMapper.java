package app.api.mappers;
import app.api.dto.request.TrainerRegistrationRequest;
import app.api.dto.response.TraineeSummaryResponse;
import app.api.dto.response.TrainerProfileResponse;
import app.api.dto.response.TrainerSummaryResponse;
import app.api.dto.response.TrainingTypeResponse;
import app.domain.entities.Trainer;
import app.domain.entities.TrainingType;
import app.domain.entities.User;
import org.springframework.stereotype.Component;

@Component
public class TrainerApiMapper {

    public Trainer toTrainer(TrainerRegistrationRequest trainerRegistrationRequest, TrainingType trainingType) {
        User user = new User();
        user.setFirstName(trainerRegistrationRequest.getFirstName());
        user.setLastName(trainerRegistrationRequest.getLastName());
        user.setActive(true);
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setTrainingType(trainingType);
        return trainer;
    }

    public TrainerProfileResponse toTrainerProfileResponse(Trainer trainer) {
        return new TrainerProfileResponse(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                new TrainingTypeResponse(trainer.getTrainingType().getName(), trainer.getTrainingType().getId()),
                trainer.getUser().isActive(),
                trainer.getTrainees().stream().map(tr ->
                        new TraineeSummaryResponse(tr.getUser().getUsername(),
                                tr.getUser().getFirstName(), tr.getUser().getLastName()))
                        .toList()
        );
    }


    public TrainerSummaryResponse toTrainerSummaryResponse(Trainer trainer) {
        return new TrainerSummaryResponse(
                trainer.getUser().getUsername(), trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(), new TrainingTypeResponse(trainer.getTrainingType().getName(),
                trainer.getTrainingType().getId())
        );
    }

}
