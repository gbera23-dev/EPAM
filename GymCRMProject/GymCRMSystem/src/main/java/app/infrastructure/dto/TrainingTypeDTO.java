package app.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TrainingTypeDTO {
    private long id;
    @NotBlank(message = "Provided name must not be Blank!")
    @NotNull(message = "Provided name must not be null!")
    private String name;
}
