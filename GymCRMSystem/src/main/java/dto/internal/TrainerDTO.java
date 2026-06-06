package dto.internal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class TrainerDTO implements GymDTO {

    private long trainerPk;
    @NotBlank(message="Specialization cannot be blank!")
    @NotNull(message="Specialization cannot be null!")
    private String specialization;
    @NotNull(message="UserDTO cannot be null!")
    @Valid
    private UserDTO user;

    @Override
    public long getEntityId() {
        return this.trainerPk;
    }


}
