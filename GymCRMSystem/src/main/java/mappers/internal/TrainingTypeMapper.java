package mappers.internal;

import dto.internal.TrainingTypeDTO;
import entities.TrainingType;
import org.springframework.stereotype.Component;

@Component("TrainingTypeMapper")
public class TrainingTypeMapper implements Mapper<TrainingTypeDTO, TrainingType> {

    @Override
    public TrainingTypeDTO toDTO(TrainingType trainingType) {
        TrainingTypeDTO dto = new TrainingTypeDTO();
        dto.setId(trainingType.getId());
        dto.setName(trainingType.getName());
        return dto;
    }

    @Override
    public TrainingType toEntity(TrainingTypeDTO dto) {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(null);
        trainingType.setName(dto.getName());
        return trainingType;
    }
}