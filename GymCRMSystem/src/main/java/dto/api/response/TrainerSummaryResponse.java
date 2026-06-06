package dto.api.response;

import entities.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TrainerSummaryResponse {

    private String username;
    private String firstName;
    private String lastName;
    private TrainingType specialization;
}
