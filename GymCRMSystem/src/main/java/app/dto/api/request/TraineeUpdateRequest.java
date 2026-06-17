package app.dto.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TraineeUpdateRequest {

    @NotBlank(message="username is required!")
    private String username;
    @NotBlank(message="first name is required!")
    private String firstName;
    @NotBlank(message="date of birth is required!")
    private String lastName;

    private LocalDate dateOfBirth;

    private String address;

    private Boolean isActive;
}
