package dto.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TrainingTypeDTO {
    private long id;
    @NotBlank(message = "Provided name must not be Blank!")
    @NotNull(message = "Provided name must not be null!")
    private String name;
}
