package dto.api.request;


import entities.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerRegistrationRequest {

    @NotBlank(message="first name is required!")
    String firstName;

    @NotBlank(message="last name is required!")
    String lastName;

    @NotNull(message="specialization is required!")
    TrainingType specialization;

}
