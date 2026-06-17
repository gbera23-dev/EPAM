package app.dto.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class TraineeTrainingsRequest {

    @NotBlank
    private String username;

    private LocalDate from;

    private LocalDate to;

    private String trainerName;

    private TrainingTypeRequest trainingTypeRequest;
}
