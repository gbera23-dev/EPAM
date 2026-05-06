package Builders;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Trainer;
import entities.User;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("TrainerBuilder")
public class TrainerBuilder implements Builder {

    private ObjectMapper objectMapper;

    public TrainerBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Trainer build(Map<String, Object> entry) {
        return new Trainer(
                ((Number) entry.get("trainerPK")).longValue(),
                (String)entry.get("specialization"),
                objectMapper.convertValue(entry.get("user"), User.class)
        );
    }


}
