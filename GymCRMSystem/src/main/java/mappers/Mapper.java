package mappers;

import dto.GymDTO;
import entities.GymEntity;

public interface Mapper<D, E> {

    D toDTO(E gymEntity);

    E toEntity(D gymDTO);


}
