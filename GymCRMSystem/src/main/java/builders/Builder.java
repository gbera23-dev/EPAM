package builders;

import entities.GymEntity;

import java.util.Map;

/**
 * The Builder interface provides a contract for EntityBuilders, which, given the Map, produce appropriate
 * GymEntity instance
 */
public interface Builder {

    /**
     * Method builds a particular implementation of GymEntity interface based on the contents of the Map
     * @param entry Map of entries
     * @return One of the implementations of GymEntity's instance(Trainee, Trainer, Training)
     */
    GymEntity build(Map<String, Object> entry);
}
