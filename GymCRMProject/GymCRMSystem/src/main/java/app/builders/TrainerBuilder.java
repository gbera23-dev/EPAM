package app.builders;

import com.fasterxml.jackson.databind.ObjectMapper;
import app.dto.internal.TrainerDTO;
import app.dto.internal.UserDTO;
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
        return new TrainerDTO(
                ((Number) entry.get("trainerPK")).longValue(),
                (String)entry.get("specialization"),
                objectMapper.convertValue(entry.get("user"), UserDTO.class)
        );
    }


}