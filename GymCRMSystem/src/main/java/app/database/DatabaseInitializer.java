package app.database;

import app.dto.internal.TraineeDTO;
import app.dto.internal.TrainerDTO;
import app.dto.internal.TrainingDTO;
import app.entities.*;
import app.mappers.internal.GymMapper;
import app.mappers.internal.Mapper;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import app.persistence.GymRepository;

import java.util.Map;
import java.util.stream.Collectors;


/**
 * Initializes the database with data on application startup, when database is empty
 */
@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final GymRepository repositories;
    private final GymMapper mappers;
    private final Map<String, Map<Long, ?>> storages;

    /**
     * Constructs a {@code DatabaseInitializer} with the required dependencies.
     *
     * @param repositories the wrapper providing access to all JPA repositories
     * @param mappers      the wrapper providing DTO-to-entity mappers
     * @param storages     the named in-memory storages containing seed DTOs
     */
    public DatabaseInitializer(GymRepository repositories,
                               GymMapper mappers,
                               Map<String, Map<Long, ?>> storages) {
        this.repositories = repositories;
        this.mappers = mappers;
        this.storages = storages;
    }

    /**
     * Persists data present in storage beans into database
     * <p>Initialization is skipped if:
     * <ul>
     *   <li>The refreshed context has a parent context (child context refresh), or</li>
     *   <li>The {@code users} table already contains records.</li>
     * </ul>
     * When neither condition applies, trainee, trainer, and training entities are
     * converted from their respective storages and persisted.</p>
     *
     * @param event the context refreshed event fired by Spring
     */
    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null || repositories.getUserRepository().count() > 0) {
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

    /**
     * Persists all converted entity maps to the database via their respective repositories.
     *
     * @param trainerMap  map of trainer entities keyed by their storage ID
     * @param traineeMap  map of trainee entities keyed by their storage ID
     * @param trainingMap map of training entities keyed by their storage ID
     */
    @SuppressWarnings("unchecked")
    private void persistData(Map<Long, Trainer> trainerMap, Map<Long, Trainee> traineeMap,
                             Map<Long, Training> trainingMap) {
        repositories.getTraineeRepository().saveAll(traineeMap.values());
        repositories.getTrainerRepository().saveAll(trainerMap.values());
        repositories.getTrainingRepository().saveAll(trainingMap.values());
    }


    /**
            * Converts the {@code TrainingStorage} DTOs into {@link Training} entities,
            * resolving and assigning the associated {@link Trainee} and {@link Trainer}
     * from the already-converted entity maps.
     *
             * @param traineeMap map of trainee entities keyed by storage ID, used to resolve
     *                   each training's trainee reference
            * @param trainerMap map of trainer entities keyed by storage ID, used to resolve
     *                   each training's trainer reference
            * @return a map of {@link Training} entities keyed by their storage ID
     */
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


    /**
     * Converts a generic DTO storage map into a map of JPA entities using the provided mapper.
     *
     * @param <D>     the DTO type
     * @param <E>     the entity type
     * @param storage the map of DTOs keyed by storage ID
     * @param mapper  the mapper used to convert each DTO to its corresponding entity
     * @return a map of entities keyed by the same storage IDs as the input
     */
    private <D, E> Map<Long, E> convertStorageToEntityMap(Map<Long, D> storage, Mapper<D, E> mapper) {
        return storage.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> mapper.toEntity(e.getValue())
                ));
    }




}
