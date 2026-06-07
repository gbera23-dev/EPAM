package dto.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class TraineeTrainersUpdateRequest {

    @NotBlank
    String traineeUsername;

    @NotNull
    List<String> trainerUsernames;

}
