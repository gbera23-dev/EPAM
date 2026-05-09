package builders;

import entities.GymEntity;

import java.util.Map;


public interface Builder {

    GymEntity build(Map<String, Object> entry);

}
