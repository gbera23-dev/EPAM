package Database;

import dto.TraineeDTO;
import dto.TrainerDTO;
import dto.TrainingDTO;
import dto.UserDTO;
import entities.Trainee;
import entities.Trainer;
import entities.Training;
import entities.User;
import mappers.Mapper; // Your generic interface
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import persistence.TraineeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final TraineeRepository traineeRepository;
    private final Mapper<TraineeDTO, Trainee> traineeMapper;
    private final Mapper<TrainerDTO, Trainer> trainerMapper;
    private final Mapper<TrainingDTO, Training> trainingMapper;
    private final Mapper<UserDTO, User> userMapper;


    private final Map<Long, TraineeDTO> traineeStorage;
    private final Map<Long, TrainerDTO> trainerStorage;
    private final Map<Long, TrainingDTO> trainingStorage;



    public DatabaseInitializer(TraineeRepository traineeRepository,
                               Mapper<TraineeDTO, Trainee> traineeMapper,
                               Mapper<TrainerDTO, Trainer> trainerMapper,
                               Mapper<TrainingDTO, Training> trainingMapper,
                               Mapper<UserDTO, User> userMapper,
                               Map<Long, TraineeDTO> traineeStorage,
                               Map<Long, TrainerDTO> trainerStorage,
                               Map<Long, TrainingDTO> trainingStorage) {
        this.traineeRepository = traineeRepository;
        this.traineeMapper = traineeMapper;
        this.trainerMapper = trainerMapper;
        this.trainingMapper = trainingMapper;
        this.userMapper = userMapper;
        this.traineeStorage = traineeStorage;
        this.trainerStorage = trainerStorage;
        this.trainingStorage = trainingStorage;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null) {
            return;
        }

        if (traineeRepository.count() == 0) {
            List<Trainee> traineeList = traineeStorage
                    .values()
                    .stream()
                    .map(traineeMapper::toEntity)
                    .toList();


        }
    }
}
