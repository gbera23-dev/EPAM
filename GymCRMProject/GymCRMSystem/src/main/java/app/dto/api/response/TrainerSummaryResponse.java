package app.dto.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerSummaryResponse {

    private String username;
    private String firstName;
    private String lastName;
    private TrainingTypeResponse specialization;
}
