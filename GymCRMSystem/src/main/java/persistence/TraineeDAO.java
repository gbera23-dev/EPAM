package persistence;
import entities.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TraineeDAO extends AbstractDAO<Trainee> {

    @Autowired
    @Override
    public void setStorage(Map<Long, Trainee> storage) {
        super.setStorage(storage);
    }


}
