package mappers;

/**
 * Bidirectional mapper between a DTO and its corresponding entity.
 *
 * @param <D> DTO type
 * @param <E> entity type
 */
public interface Mapper<D, E> {

    /** @return DTO representation of the given entity */
    D toDTO(E entity);

    /** @return entity representation of the given DTO */
    E toEntity(D dto);
}