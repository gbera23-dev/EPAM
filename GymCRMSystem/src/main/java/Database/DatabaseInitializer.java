package Database;

import dto.*;
import entities.*;
import mappers.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import persistence.TraineeRepository;
import persistence.TrainerRepository;
import persistence.TrainingRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    Map<String, JpaRepository<?, Long>> repositories;

    Map<String, Mapper<?, ?>> mappers;

    Map<String, Map<Long, ?>> storages;

    @Value("${database.initialize}")
    private boolean databaseInit;

    public DatabaseInitializer(Map<String, JpaRepository<?, Long>> repositories,
                               Map<String, Mapper<?, ?>> mappers,
                               Map<String, Map<Long, ?>> storages
                               ) {
        this.repositories = repositories;
        this.mappers = mappers;
        this.storages = storages;
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null) {
            return;
        }

        if (databaseInit) {
            Mapper<TraineeDTO, Trainee> traineeMapper = (Mapper<TraineeDTO, Trainee>) mappers.get("TraineeMapper");
            Mapper<TrainerDTO, Trainer> trainerMapper = (Mapper<TrainerDTO, Trainer>) mappers.get("TrainerMapper");
            Mapper<TrainingDTO, Training> trainingMapper = (Mapper<TrainingDTO, Training>) mappers.get("TrainingMapper");

            Map<Long, TraineeDTO> traineeStorage = (Map<Long, TraineeDTO>) storages.get("TraineeStorage");
            Map<Long, TrainerDTO> trainerStorage = (Map<Long, TrainerDTO>) storages.get("TrainerStorage");
            Map<Long, TrainingDTO> trainingStorage = (Map<Long, TrainingDTO>) storages.get("TrainingStorage");


            Map<Long, Trainee> traineeMap = convertStorageToEntityMap(traineeStorage, traineeMapper);

            Map<Long, Trainer> trainerMap = convertStorageToEntityMap(trainerStorage, trainerMapper);

            Map<Long, Training> trainingMap = trainingStorage.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> {
                                Training training = trainingMapper.toEntity(e.getValue());
                                training.setTrainee(traineeMap.get(e.getValue().getTraineeId()));
                                training.setTrainer(trainerMap.get(e.getValue().getTrainerId()));
                                return training;
                            }
                    ));

            JpaRepository<Trainee, Long> traineeRepository = (JpaRepository<Trainee, Long>)
                    repositories.get("TraineeRepository");

            JpaRepository<Trainer, Long> trainerRepository = (JpaRepository<Trainer, Long>)
                    repositories.get("TrainerRepository");

            JpaRepository<Training, Long> trainingRepository = (JpaRepository<Training, Long>)
                    repositories.get("TrainingRepository");

            traineeRepository.saveAll(traineeMap.values());
            trainerRepository.saveAll(trainerMap.values());
            trainingRepository.saveAll(trainingMap.values());
        }
    }

    private <D, E> Map<Long, E> convertStorageToEntityMap(Map<Long, D> storage, Mapper<D, E> mapper) {
        return storage.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> mapper.toEntity(e.getValue())
                ));
    }




}
