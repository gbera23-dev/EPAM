package builders;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.TrainingDTO;
import dto.TrainingTypeDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
@Component("TrainingBuilder")
public class TrainingBuilder implements Builder {


    private final ObjectMapper objectMapper;

    public TrainingBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public TrainingDTO build(Map<String, Object> entry) {
        return new TrainingDTO(
                ((Number) entry.get("trainingPK")).longValue(),
                ((Number) entry.get("traineeId")).longValue(),
                ((Number) entry.get("trainerId")).longValue(),
                (String)entry.get("name"),
                objectMapper.convertValue(entry.get("trainingType"), TrainingTypeDTO.class),
                objectMapper.convertValue(entry.get("date"), LocalDate.class),
                (int)entry.get("duration")
        );
    }

}