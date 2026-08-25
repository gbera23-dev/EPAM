package app.api.mappers;

import app.api.dto.request.TraineeRegistrationRequest;
import app.api.dto.response.TraineeProfileResponse;
import app.domain.entities.Trainee;
import app.domain.entities.User;
import org.springframework.stereotype.Component;


@Component
public class TraineeApiMapper {

    private final TrainerApiMapper trainerApiMapper;


    public TraineeApiMapper(TrainerApiMapper trainerApiMapper) {
        this.trainerApiMapper = trainerApiMapper;
    }

     public Trainee toTrainee(TraineeRegistrationRequest traineeRegistrationRequest) {
         User user = new User();
         user.setFirstName(traineeRegistrationRequest.getFirstName());
         user.setLastName(traineeRegistrationRequest.getLastName());
         user.setActive(true);
         Trainee trainee = new Trainee();
         trainee.setUser(user);
         trainee.setAddress(traineeRegistrationRequest.getAddress());
         trainee.setDateOfBirth(traineeRegistrationRequest.getDateOfBirth());
         return trainee;
     }

     public TraineeProfileResponse toTraineeProfileResponse(Trainee trainee) {
        return new TraineeProfileResponse(
                trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getUser().isActive(),
                trainee.getTrainers().stream().map(trainerApiMapper::toTrainerSummaryResponse)
                        .toList()
        );
     }

}
