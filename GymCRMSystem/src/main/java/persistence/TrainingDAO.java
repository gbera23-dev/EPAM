package persistence;
import entities.Trainer;
import entities.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TrainingDAO extends AbstractDAO<Training> {

    @Autowired
    @Override
    public void setStorage(Map<Long, Training> storage) {
        super.setStorage(storage);
    }

}
