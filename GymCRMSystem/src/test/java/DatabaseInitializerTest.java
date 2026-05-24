import Database.DatabaseInitializer;
import dto.TraineeDTO;
import dto.TrainerDTO;
import dto.TrainingDTO;
import entities.Trainee;
import entities.Trainer;
import entities.Training;
import mappers.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DatabaseInitializerTest {

    @Mock
    private Mapper<TraineeDTO, Trainee> traineeMapper;

    @Mock
    private Mapper<TrainerDTO, Trainer> trainerMapper;

    @Mock
    private Mapper<TrainingDTO, Training> trainingMapper;

    @Mock
    private JpaRepository<Trainee, Long> traineeRepository;

    @Mock
    private JpaRepository<Trainer, Long> trainerRepository;

    @Mock
    private JpaRepository<Training, Long> trainingRepository;

    @Mock
    private ContextRefreshedEvent event;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ApplicationContext parentContext;

    @InjectMocks
    private DatabaseInitializer databaseInitializer;

    private Map<String, JpaRepository<?, Long>> repositories;
    private Map<String, Mapper<?, ?>> mappers;
    private Map<String, Map<Long, ?>> storages;

    private Map<Long, TraineeDTO> traineeStorage;
    private Map<Long, TrainerDTO> trainerStorage;
    private Map<Long, TrainingDTO> trainingStorage;

    @BeforeEach
    void setUp() {
        repositories = new HashMap<>();
        repositories.put("TraineeRepository", traineeRepository);
        repositories.put("TrainerRepository", trainerRepository);
        repositories.put("TrainingRepository", trainingRepository);

        mappers = new HashMap<>();
        mappers.put("TraineeMapper", traineeMapper);
        mappers.put("TrainerMapper", trainerMapper);
        mappers.put("TrainingMapper", trainingMapper);

        traineeStorage = new HashMap<>();
        trainerStorage = new HashMap<>();
        trainingStorage = new HashMap<>();

        storages = new HashMap<>();
        storages.put("TraineeStorage", traineeStorage);
        storages.put("TrainerStorage", trainerStorage);
        storages.put("TrainingStorage", trainingStorage);

        databaseInitializer = new DatabaseInitializer(repositories, mappers, storages);

        when(event.getApplicationContext()).thenReturn(applicationContext);
    }

    private void enableDatabaseInit() throws NoSuchFieldException, IllegalAccessException {
        java.lang.reflect.Field field = DatabaseInitializer.class.getDeclaredField("databaseInit");
        field.setAccessible(true);
        field.set(databaseInitializer, true);
    }

    private void disableDatabaseInit() throws NoSuchFieldException, IllegalAccessException {
        java.lang.reflect.Field field = DatabaseInitializer.class.getDeclaredField("databaseInit");
        field.setAccessible(true);
        field.set(databaseInitializer, true);
    }

    @Test
    void testOnApplicationEventSkipsAllProcessingWhenParentContextExists() throws NoSuchFieldException, IllegalAccessException {
        when(applicationContext.getParent()).thenReturn(parentContext);
        enableDatabaseInit();

        databaseInitializer.onApplicationEvent(event);

        verifyNoInteractions(traineeRepository, trainerRepository, trainingRepository);
    }

    @Test
    void testOnApplicationEventDoesNotInvokeAnyMapperWhenParentContextExists() throws NoSuchFieldException, IllegalAccessException {
        when(applicationContext.getParent()).thenReturn(parentContext);
        enableDatabaseInit();

        databaseInitializer.onApplicationEvent(event);

        verifyNoInteractions(traineeMapper, trainerMapper, trainingMapper);
    }

    @Test
    void testOnApplicationEventDoesNotInvokeAnyMapperWhenDatabaseInitIsFalse() throws NoSuchFieldException, IllegalAccessException {
        when(applicationContext.getParent()).thenReturn(null);
        disableDatabaseInit();

        databaseInitializer.onApplicationEvent(event);

        verifyNoInteractions(traineeMapper, trainerMapper, trainingMapper);
    }

    @Test
    void testOnApplicationEventSavesEmptyCollectionsWhenStoragesAreEmpty() throws NoSuchFieldException, IllegalAccessException {
        when(applicationContext.getParent()).thenReturn(null);
        enableDatabaseInit();

        databaseInitializer.onApplicationEvent(event);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<Trainee>> traineeCaptor = ArgumentCaptor.forClass(Collection.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<Trainer>> trainerCaptor = ArgumentCaptor.forClass(Collection.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<Training>> trainingCaptor = ArgumentCaptor.forClass(Collection.class);

        verify(traineeRepository).saveAll(traineeCaptor.capture());
        verify(trainerRepository).saveAll(trainerCaptor.capture());
        verify(trainingRepository).saveAll(trainingCaptor.capture());

        assertTrue(traineeCaptor.getValue().isEmpty());
        assertTrue(trainerCaptor.getValue().isEmpty());
        assertTrue(trainingCaptor.getValue().isEmpty());
    }

    @Test
    void testOnApplicationEventInvokesSaveAllOncePerRepository() throws NoSuchFieldException, IllegalAccessException {
        when(applicationContext.getParent()).thenReturn(null);
        enableDatabaseInit();

        databaseInitializer.onApplicationEvent(event);

        verify(traineeRepository, times(1)).saveAll(any());
        verify(trainerRepository, times(1)).saveAll(any());
        verify(trainingRepository, times(1)).saveAll(any());
    }

    @Test
    void testSavesEntities() throws NoSuchFieldException, IllegalAccessException {
        Map<String, Mapper<?, ?>> fullMappers = new HashMap<>();
        fullMappers.put("mappers.TraineeMapper", traineeMapper);
        fullMappers.put("mappers.TrainerMapper", trainerMapper);
        fullMappers.put("mappers.TrainingMapper", trainingMapper);

        TraineeDTO traineeDTO = new TraineeDTO();
        TrainerDTO trainerDTO = new TrainerDTO();

        traineeStorage.put(1L, traineeDTO);
        trainerStorage.put(1L, trainerDTO);

        databaseInitializer = new DatabaseInitializer(repositories, fullMappers, storages);

        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();

        when(traineeMapper.toEntity(traineeDTO)).thenReturn(trainee);
        when(trainerMapper.toEntity(trainerDTO)).thenReturn(trainer);
        when(event.getApplicationContext()).thenReturn(applicationContext);
        when(applicationContext.getParent()).thenReturn(null);

        java.lang.reflect.Field field = DatabaseInitializer.class.getDeclaredField("databaseInit");
        field.setAccessible(true);
        field.set(databaseInitializer, true);

        databaseInitializer.onApplicationEvent(event);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<Trainee>> traineeCaptor = ArgumentCaptor.forClass(Collection.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<Trainer>> trainerCaptor = ArgumentCaptor.forClass(Collection.class);

        verify(traineeRepository).saveAll(traineeCaptor.capture());
        verify(trainerRepository).saveAll(trainerCaptor.capture());

        assertTrue(traineeCaptor.getValue().contains(trainee));
        assertTrue(trainerCaptor.getValue().contains(trainer));
    }

    @Test
    void testSavesTrainings() throws NoSuchFieldException, IllegalAccessException {
        Map<String, Mapper<?, ?>> fullMappers = new HashMap<>();
        fullMappers.put("mappers.TraineeMapper", traineeMapper);
        fullMappers.put("mappers.TrainerMapper", trainerMapper);
        fullMappers.put("mappers.TrainingMapper", trainingMapper);

        TraineeDTO traineeDTO = new TraineeDTO();
        TrainerDTO trainerDTO = new TrainerDTO();
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTraineeId(1L);
        trainingDTO.setTrainerId(1L);

        traineeStorage.put(1L, traineeDTO);
        trainerStorage.put(1L, trainerDTO);
        trainingStorage.put(1L, trainingDTO);

        databaseInitializer = new DatabaseInitializer(repositories, fullMappers, storages);

        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        Training training = new Training();

        when(traineeMapper.toEntity(traineeDTO)).thenReturn(trainee);
        when(trainerMapper.toEntity(trainerDTO)).thenReturn(trainer);
        when(trainingMapper.toEntity(trainingDTO)).thenReturn(training);
        when(event.getApplicationContext()).thenReturn(applicationContext);
        when(applicationContext.getParent()).thenReturn(null);

        java.lang.reflect.Field field = DatabaseInitializer.class.getDeclaredField("databaseInit");
        field.setAccessible(true);
        field.set(databaseInitializer, true);

        databaseInitializer.onApplicationEvent(event);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<Training>> trainingCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(trainingRepository).saveAll(trainingCaptor.capture());

        assertTrue(trainingCaptor.getValue().contains(training));
        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
    }
}