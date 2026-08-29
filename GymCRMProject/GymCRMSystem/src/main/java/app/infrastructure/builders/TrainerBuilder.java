package app.infrastructure.builders;

import com.fasterxml.jackson.databind.ObjectMapper;
import app.infrastructure.dto.TrainerDTO;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("TrainerBuilder")
public class TrainerBuilder implements Builder {

    private final ObjectMapper objectMapper;

    public TrainerBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public TrainerDTO build(Map<String, Object> entry) {
        return objectMapper.convertValue(entry, TrainerDTO.class);
    }

}