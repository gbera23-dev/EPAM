package dto.api.response;

import entities.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
public class TrainingResponse {

    private String trainingName;

    private LocalDate date;

    private TrainingTypeResponse trainingTypeResponse;

    private int duration;

    private String trainerName;

}
