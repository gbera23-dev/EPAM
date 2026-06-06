package mappers.api;

import dto.api.request.TraineeRegistrationRequest;
import dto.api.request.TraineeUpdateRequest;
import dto.api.response.TraineeProfileResponse;
import dto.api.response.TrainerSummaryResponse;
import entities.Trainee;
import entities.User;
import org.springframework.stereotype.Component;

@Component
public class TraineeApiMapper {

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
                trainee.getTrainers().stream().map(t -> new TrainerSummaryResponse(
                        t.getUser().getUsername(), t.getUser().getFirstName(), t.getUser().getLastName(), t.getTrainingType()
                )).toList()
        );
     }

}
