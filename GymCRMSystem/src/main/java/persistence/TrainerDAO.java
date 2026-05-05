package persistence;

import entities.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class TrainerDAO extends AbstractDAO<Trainer> {

    @Autowired
    @Override
    public void setStorage(Map<Long, Trainer> storage) {
        super.setStorage(storage);
    }

}
