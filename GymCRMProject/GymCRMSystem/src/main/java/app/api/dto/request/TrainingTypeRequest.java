package app.api.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TrainingTypeRequest {

    private long trainingTypeId;
    private String trainingTypeName;

}
