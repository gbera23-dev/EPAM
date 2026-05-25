package database;

import dto.*;
import entities.*;
import mappers.GymMapper;
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
    private final GymMapper mappers;
    private final Map<String, Map<Long, ?>> storages;

    @Value("${database.initialize}")
    private boolean databaseInit;

    public DatabaseInitializer(Map<String, JpaRepository<?, Long>> repositories,
                               GymMapper mappers,
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

        Map<Long, TraineeDTO> traineeStorage =
                (Map<Long, TraineeDTO>) storages.get("TraineeStorage");

        Map<Long, TrainerDTO> trainerStorage =
                (Map<Long, TrainerDTO>) storages.get("TrainerStorage");

        Map<Long, Trainee> traineeMap = convertStorageToEntityMap(traineeStorage, mappers.getTraineeMapper());

        Map<Long, Trainer> trainerMap = convertStorageToEntityMap(trainerStorage, mappers.getTrainerMapper());

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

        return trainingStorage.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            Training training = mappers.getTrainingMapper().toEntity(e.getValue());
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
