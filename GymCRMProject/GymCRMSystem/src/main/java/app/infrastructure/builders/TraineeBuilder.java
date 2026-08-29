package app.infrastructure.builders;

import com.fasterxml.jackson.databind.ObjectMapper;
import app.infrastructure.dto.TraineeDTO;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("TraineeBuilder")
public class TraineeBuilder implements Builder {

    private final ObjectMapper objectMapper;

    public TraineeBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public TraineeDTO build(Map<String, Object> entry) {

        return objectMapper.convertValue(entry, TraineeDTO.class);
    }

}