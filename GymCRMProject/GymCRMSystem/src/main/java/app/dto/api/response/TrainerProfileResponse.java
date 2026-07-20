package app.dto.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerProfileResponse {

    private String firstName;
    private String lastName;
    private TrainingTypeResponse specialization;
    private boolean isActive;
    private List<TraineeSummaryResponse> traineeSummaryResponseList;
}
