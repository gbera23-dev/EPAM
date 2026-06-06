package dto.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TraineeUpdateRequest {

    @NotBlank(message="username is required!")
    private String username;
    @NotBlank(message="first name is required!")
    private String firstName;
    @NotBlank(message="date of birth is required!")
    private String lastName;

    private LocalDate dateOfBirth;

    private String address;

    private boolean isActive;
}
