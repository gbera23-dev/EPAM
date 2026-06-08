package app.mappers.internal;

import app.dto.internal.TrainingDTO;
import app.entities.Training;
import org.springframework.stereotype.Component;

@Component("TrainingMapper")
public class TrainingMapper implements Mapper<TrainingDTO, Training> {

    private final TrainingTypeMapper trainingTypeMapper;

    public TrainingMapper(TrainingTypeMapper trainingTypeMapper) {
        this.trainingTypeMapper = trainingTypeMapper;
    }

    @Override
    public TrainingDTO toDTO(Training training) {
        TrainingDTO dto = new TrainingDTO();
        dto.setTrainingPk(training.getId());
        dto.setTraineeId(training.getTrainee().getId());
        dto.setTrainerId(training.getTrainer().getId());
        dto.setName(training.getName());
        dto.setTrainingTypeDto(trainingTypeMapper.toDTO(training.getTrainingType()));
        dto.setDate(training.getDate());
        dto.setDuration(training.getDuration());
        return dto;
    }

    @Override
    public Training toEntity(TrainingDTO dto) {
        Training training = new Training();
        training.setId(null);
        training.setTrainee(null);
        training.setTrainer(null);
        training.setName(dto.getName());
        training.setTrainingType(trainingTypeMapper.toEntity(dto.getTrainingTypeDto()));
        training.setDate(dto.getDate());
        training.setDuration(dto.getDuration());
        return training;
    }
}