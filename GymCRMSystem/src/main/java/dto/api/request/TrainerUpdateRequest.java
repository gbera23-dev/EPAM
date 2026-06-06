package dto.api.request;

import entities.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerUpdateRequest {
    @NotBlank(message="username is required!")
    private String username;
    @NotBlank(message="first name is required!")
    private String firstName;
    @NotBlank(message="last name is required!")
    private String lastName;

    private TrainingType specialization;
    @NotNull(message="is active value is required!")
    private Boolean isActive;
}
