package dto.internal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class TraineeDTO implements GymDTO {

    private long TraineePk;
    @NotNull(message="Date of birth cannot be null!")
    private LocalDate dateOfBirth;
    @NotBlank(message="Address cannot be empty!")
    @NotNull(message="Address cannot be null!")
    private String address;
    @NotNull(message="UserDTO cannot be null!")
    @Valid
    private UserDTO user;
    @Override
    public long getEntityId() {
        return this.TraineePk;
    }
}