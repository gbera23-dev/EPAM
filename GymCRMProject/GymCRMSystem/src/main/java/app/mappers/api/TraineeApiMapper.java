package app.mappers.api;

import app.dto.api.request.TraineeRegistrationRequest;
import app.dto.api.response.TraineeProfileResponse;
import app.entities.Trainee;
import app.entities.User;
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
