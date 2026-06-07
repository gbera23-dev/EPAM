package dto.api.response;


import entities.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TrainingTypeResponse {

    private String trainingTypeName;

    private long trainingTypeId;

}
