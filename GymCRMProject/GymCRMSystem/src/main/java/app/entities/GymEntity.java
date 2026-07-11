package app.entities;

/** Marker interface for all persistable gym domain entities. */
public interface GymEntity {

    /** @return the entity's primary key */
    long getEntityId();

}
