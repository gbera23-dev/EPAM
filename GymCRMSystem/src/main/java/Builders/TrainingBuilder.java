package Builders;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Training;
import entities.TrainingType;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
@Component("TrainingBuilder")
public class TrainingBuilder implements Builder {


    private ObjectMapper objectMapper;

    public TrainingBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Training build(Map<String, Object> entry) {
        return new Training(
                ((Number) entry.get("trainingPK")).longValue(),
                ((Number) entry.get("traineeId")).longValue(),
                ((Number) entry.get("trainerId")).longValue(),
                (String)entry.get("name"),
                objectMapper.convertValue(entry.get("trainingType"), TrainingType.class),
                objectMapper.convertValue(entry.get("date"), Date.class),
                (int)entry.get("duration")
        );
    }

}
