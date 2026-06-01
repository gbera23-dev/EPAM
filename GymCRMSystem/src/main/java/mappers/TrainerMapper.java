package mappers;

import dto.TrainerDTO;
import entities.Trainer;
import entities.TrainingType;
import org.springframework.stereotype.Component;

@Component("TrainerMapper")
public class TrainerMapper implements Mapper<TrainerDTO, Trainer> {

    private final UserMapper userMapper;

    public TrainerMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public TrainerDTO toDTO(Trainer trainer) {
        TrainerDTO dto = new TrainerDTO();
        dto.setTrainerPk(trainer.getId());
        dto.setSpecialization(trainer.getTrainingType().getName());
        dto.setUser(userMapper.toDTO(trainer.getUser()));
        return dto;
    }

    @Override
    public Trainer toEntity(TrainerDTO dto) {
        Trainer trainer = new Trainer();
        trainer.setId(null);
        TrainingType trainingType = new TrainingType();
        trainingType.setName(dto.getSpecialization());
        trainer.setTrainingType(trainingType);
        trainer.setUser(userMapper.toEntity(dto.getUser()));
        return trainer;
    }
}