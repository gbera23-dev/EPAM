package app.dto.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainingResponse {

    private String trainingName;

    private LocalDate date;

    private TrainingTypeResponse trainingTypeResponse;

    private int duration;

    private String trainerName;

}
