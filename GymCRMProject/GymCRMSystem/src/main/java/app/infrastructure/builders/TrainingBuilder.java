package app.infrastructure.builders;

import com.fasterxml.jackson.databind.ObjectMapper;
import app.infrastructure.dto.TrainingDTO;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component("TrainingBuilder")
public class TrainingBuilder implements Builder {


    private final ObjectMapper objectMapper;

    public TrainingBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public TrainingDTO build(Map<String, Object> entry) {
        return objectMapper.convertValue(entry, TrainingDTO.class);
    }

}