package app.mappers.api;
import app.dto.api.request.TrainerRegistrationRequest;
import app.dto.api.response.TraineeSummaryResponse;
import app.dto.api.response.TrainerProfileResponse;
import app.dto.api.response.TrainerSummaryResponse;
import app.dto.api.response.TrainingTypeResponse;
import app.entities.Trainer;
import app.entities.TrainingType;
import app.entities.User;
import org.springframework.stereotype.Component;

@Component
public class TrainerApiMapper {

    public Trainer toTrainer(TrainerRegistrationRequest trainerRegistrationRequest, TrainingType trainingType) {
        User user = new User();
        user.setFirstName(trainerRegistrationRequest.getFirstName());
        user.setLastName(trainerRegistrationRequest.getLastName());
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
