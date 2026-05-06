package Builders;

import com.fasterxml.jackson.databind.ObjectMapper;
import entities.Trainee;
import entities.User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component("TraineeBuilder")
public class TraineeBuilder implements Builder {

    private ObjectMapper objectMapper;

    public TraineeBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Trainee build(Map<String, Object> entry) {
        return new Trainee(
                ((Number) entry.get("traineePK")).longValue(),
                objectMapper.convertValue(entry.get("dateOfBirth"), Date.class),
                (String)entry.get("address"),
                objectMapper.convertValue(entry.get("user"), User.class)
        );
    }

}
