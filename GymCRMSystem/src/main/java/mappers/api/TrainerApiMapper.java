package mappers.api;
import dto.api.request.TrainerRegistrationRequest;
import dto.api.response.TraineeSummaryResponse;
import dto.api.response.TrainerProfileResponse;
import dto.api.response.TrainerSummaryResponse;
import entities.Trainer;
import entities.User;
import org.springframework.stereotype.Component;

@Component
public class TrainerApiMapper {

    public Trainer toTrainer(TrainerRegistrationRequest trainerRegistrationRequest) {
        User user = new User();
        user.setFirstName(trainerRegistrationRequest.getFirstName());
        user.setLastName(trainerRegistrationRequest.getLastName());
        Trainer trainer = new Trainer();
        trainer.setUser(user);
        trainer.setTrainingType(trainerRegistrationRequest.getSpecialization());
        return trainer;
    }

    public TrainerProfileResponse toTrainerProfileResponse(Trainer trainer) {
        return new TrainerProfileResponse(
                trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getTrainingType(),
                trainer.getUser().isActive(),
                trainer.getTrainees().stream().map(tr ->
                        new TraineeSummaryResponse(tr.getUser().getUsername(),
                                tr.getUser().getFirstName(), tr.getUser().getLastName()))
                        .toList()
        );
    }



}
