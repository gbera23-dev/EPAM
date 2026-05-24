package database;

import dto.*;
import entities.*;
import mappers.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final Map<String, JpaRepository<?, Long>> repositories;
    private final Map<String, Mapper<?, ?>> mappers;
    private final Map<String, Map<Long, ?>> storages;

    @Value("${database.initialize}")
    private boolean databaseInit;

    public DatabaseInitializer(Map<String, JpaRepository<?, Long>> repositories,
                               Map<String, Mapper<?, ?>> mappers,
                               Map<String, Map<Long, ?>> storages) {
        this.repositories = repositories;
        this.mappers = mappers;
        this.storages = storages;
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null || !databaseInit) {
            return;
        }

        Mapper<TraineeDTO, Trainee> traineeMapper =
                (Mapper<TraineeDTO, Trainee>) mappers.get("mappers.TraineeMapper");

        Mapper<TrainerDTO, Trainer> trainerMapper =
                (Mapper<TrainerDTO, Trainer>) mappers.get("mappers.TrainerMapper");

        Map<Long, TraineeDTO> traineeStorage =
                (Map<Long, TraineeDTO>) storages.get("TraineeStorage");

        Map<Long, TrainerDTO> trainerStorage =
                (Map<Long, TrainerDTO>) storages.get("TrainerStorage");

        Map<Long, Trainee> traineeMap = convertStorageToEntityMap(traineeStorage, traineeMapper);

        Map<Long, Trainer> trainerMap = convertStorageToEntityMap(trainerStorage, trainerMapper);

        Map<Long, Training> trainingMap = convertTrainingStorageToTrainingMap(traineeMap, trainerMap);

        persistData(trainerMap, traineeMap, trainingMap);
    }

    @SuppressWarnings("unchecked")
    private void persistData(Map<Long, Trainer> trainerMap, Map<Long, Trainee> traineeMap,
                             Map<Long, Training> trainingMap) {
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

    @SuppressWarnings("unchecked")
    private Map<Long, Training> convertTrainingStorageToTrainingMap(Map<Long, Trainee> traineeMap,
                                                             Map<Long, Trainer> trainerMap) {
        Map<Long, TrainingDTO> trainingStorage =
                (Map<Long, TrainingDTO>) storages.get("TrainingStorage");
        Mapper<TrainingDTO, Training> trainingMapper =
                (Mapper<TrainingDTO, Training>) mappers.get("mappers.TrainingMapper");

        return trainingStorage.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            Training training = trainingMapper.toEntity(e.getValue());
                            training.setTrainee(traineeMap.get(e.getValue().getTraineeId()));
                            training.setTrainer(trainerMap.get(e.getValue().getTrainerId()));
                            return training;
                        }
                ));
    }

    private <D, E> Map<Long, E> convertStorageToEntityMap(Map<Long, D> storage, Mapper<D, E> mapper) {
        return storage.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> mapper.toEntity(e.getValue())
                ));
    }




}
