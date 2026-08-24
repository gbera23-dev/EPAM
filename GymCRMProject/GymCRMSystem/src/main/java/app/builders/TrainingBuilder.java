package app.builders;

import com.fasterxml.jackson.databind.ObjectMapper;
import app.dto.internal.TrainingDTO;
import app.dto.internal.TrainingTypeDTO;
import lombok.extern.slf4j.Slf4j;
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
        return objectMapper.convertValue(entry, TrainingDTO.class);
    }

}