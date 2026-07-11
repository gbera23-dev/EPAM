import app. database.DatabaseInitializer;
import app.dto.internal.*;
import app.entities.Trainee;
import app.entities.Trainer;
import app.entities.Training;
import app.mappers.internal.GymMapper;
import app.mappers.internal.TraineeMapper;
import app.mappers.internal.TrainerMapper;
import app.mappers.internal.TrainingMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import app. persistence.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private TrainingTypeRepository trainingTypeRepository;

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
        GymRepository repositories = new GymRepository(traineeRepository, trainerRepository, trainingRepository, trainingTypeRepository, userRepository);
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

}