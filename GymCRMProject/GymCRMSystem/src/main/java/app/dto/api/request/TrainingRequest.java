package app.dto.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TrainingRequest {

    @NotBlank
    String traineeUsername;
    @NotBlank
    String trainerUsername;
    @NotBlank
    String trainingName;
    @NotNull
    LocalDate date;
    @NotNull
    @Positive
    Integer duration;
}
