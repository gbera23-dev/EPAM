import database.DatabaseInitializer;
import dto.internal.TraineeDTO;
import dto.internal.TrainerDTO;
import dto.internal.TrainingDTO;
import entities.Trainee;
import entities.Trainer;
import entities.Training;
import mappers.internal.GymMapper;
import mappers.internal.TraineeMapper;
import mappers.internal.TrainerMapper;
import mappers.internal.TrainingMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import persistence.GymRepository;
import persistence.TraineeRepository;
import persistence.TrainerRepository;
import persistence.TrainingRepository;
import persistence.UserRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DatabaseInitializerTest {

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContextRefreshedEvent event;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ApplicationContext parentContext;

    private DatabaseInitializer databaseInitializer;

    private Map<Long, TraineeDTO> traineeStorage;
    private Map<Long, TrainerDTO> trainerStorage;
    private Map<Long, TrainingDTO> trainingStorage;

    @BeforeEach
    void setUp() {
        GymRepository repositories = new GymRepository(traineeRepository, trainerRepository, trainingRepository, userRepository);
        GymMapper mappers = new GymMapper(traineeMapper, trainerMapper, trainingMapper, null, null);

        traineeStorage = new HashMap<>();
        trainerStorage = new HashMap<>();
        trainingStorage = new HashMap<>();

        Map<String, Map<Long, ?>> storages = new HashMap<>();
        storages.put("TraineeStorage", traineeStorage);
        storages.put("TrainerStorage", trainerStorage);
        storages.put("TrainingStorage", trainingStorage);

        databaseInitializer = new DatabaseInitializer(repositories, mappers, storages);

        when(event.getApplicationContext()).thenReturn(applicationContext);
    }

    @Test
    void testOnApplicationEventSkipsAllProcessingWhenParentContextExists() {
        when(applicationContext.getParent()).thenReturn(parentContext);

        databaseInitializer.onApplicationEvent(event);

        verifyNoInteractions(traineeRepository, trainerRepository, trainingRepository);
    }

    @Test
    void testOnApplicationEventDoesNotInvokeAnyMapperWhenParentContextExists() {
        when(applicationContext.getParent()).thenReturn(parentContext);

        databaseInitializer.onApplicationEvent(event);

        verifyNoInteractions(traineeMapper, trainerMapper, trainingMapper);
    }

    @Test
    void testOnApplicationEventDoesNotInvokeAnyMapperWhenDatabaseIsAlreadyInitialized() {
        when(applicationContext.getParent()).thenReturn(null);
        when(userRepository.count()).thenReturn(1L);

        databaseInitializer.onApplicationEvent(event);

        verifyNoInteractions(traineeMapper, trainerMapper, trainingMapper);
    }

    @Test
    void testOnApplicationEventSavesEmptyCollectionsWhenStoragesAreEmpty() {
        when(applicationContext.getParent()).thenReturn(null);
        when(userRepository.count()).thenReturn(0L);

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
    void testOnApplicationEventInvokesSaveAllOncePerRepository() {
        when(applicationContext.getParent()).thenReturn(null);
        when(userRepository.count()).thenReturn(0L);

        databaseInitializer.onApplicationEvent(event);

        verify(traineeRepository, times(1)).saveAll(any());
        verify(trainerRepository, times(1)).saveAll(any());
        verify(trainingRepository, times(1)).saveAll(any());
    }

    @Test
    void testSavesEntities() {
        TraineeDTO traineeDTO = new TraineeDTO();
        TrainerDTO trainerDTO = new TrainerDTO();

        traineeStorage.put(1L, traineeDTO);
        trainerStorage.put(1L, trainerDTO);

        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();

        when(traineeMapper.toEntity(traineeDTO)).thenReturn(trainee);
        when(trainerMapper.toEntity(trainerDTO)).thenReturn(trainer);
        when(applicationContext.getParent()).thenReturn(null);
        when(userRepository.count()).thenReturn(0L);

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
    void testSavesTrainings() {
        TraineeDTO traineeDTO = new TraineeDTO();
        TrainerDTO trainerDTO = new TrainerDTO();
        TrainingDTO trainingDTO = new TrainingDTO();
        trainingDTO.setTraineeId(1L);
        trainingDTO.setTrainerId(1L);

        traineeStorage.put(1L, traineeDTO);
        trainerStorage.put(1L, trainerDTO);
        trainingStorage.put(1L, trainingDTO);

        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        Training training = new Training();

        when(traineeMapper.toEntity(traineeDTO)).thenReturn(trainee);
        when(trainerMapper.toEntity(trainerDTO)).thenReturn(trainer);
        when(trainingMapper.toEntity(trainingDTO)).thenReturn(training);
        when(applicationContext.getParent()).thenReturn(null);
        when(userRepository.count()).thenReturn(0L);

        databaseInitializer.onApplicationEvent(event);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<Training>> trainingCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(trainingRepository).saveAll(trainingCaptor.capture());

        assertTrue(trainingCaptor.getValue().contains(training));
        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
    }
}