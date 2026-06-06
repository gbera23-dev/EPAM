package mappers.internal;

import dto.internal.TraineeDTO;
import entities.Trainee;
import org.springframework.stereotype.Component;

@Component("TraineeMapper")
public class TraineeMapper implements Mapper<TraineeDTO, Trainee> {

    private final UserMapper userMapper;

    public TraineeMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public TraineeDTO toDTO(Trainee trainee) {
        return new TraineeDTO(
                trainee.getId(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                userMapper.toDTO(trainee.getUser())
        );
    }

    @Override
    public Trainee toEntity(TraineeDTO dto) {
        Trainee trainee = new Trainee();
        trainee.setId(null);
        trainee.setDateOfBirth(dto.getDateOfBirth());
        trainee.setAddress(dto.getAddress());
        trainee.setUser(userMapper.toEntity(dto.getUser()));
        return trainee;
    }
}