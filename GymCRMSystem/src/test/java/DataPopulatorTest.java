import BeanPostProcessor.DataPopulator;
import Builders.Builder;
import Builders.TraineeBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import entities.GymEntity;
import entities.Trainee;
import entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataPopulatorTest {

    @Mock
    private ObjectProvider<ObjectMapper> objectMapperProvider;

    private Map<String, Resource> dataMap;
    private Map<String, Builder> builderMap;
    private DataPopulator dataPopulator;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        dataMap = new HashMap<>();
        builderMap = new HashMap<>();
        dataPopulator = new DataPopulator(dataMap, builderMap, objectMapperProvider);
    }

    @Test
    void testPostProcessBeforeInitializationSkipsNonStorageBeans() {
        Object bean = new Object();

        Object result = dataPopulator.postProcessBeforeInitialization(bean, "someOtherBean");

        assertSame(bean, result);
    }

    @Test
    void testPostProcessBeforeInitializationSkipsNonMapStorageBeans() {
        String notAMap = "notAMap";

        Object result = dataPopulator.postProcessBeforeInitialization(notAMap, "TraineeStorage");

        assertSame(notAMap, result);
    }

    @Test
    void testPostProcessBeforeInitializationPopulatesMapStorage() throws Exception {
        String json = "[{\"traineePK\":1,\"dateOfBirth\":null,\"address\":\"Addr\",\"user\":{\"userId\":1,\"firstName\":\"A\",\"lastName\":\"B\",\"username\":\"a.b\",\"password\":\"pw\",\"isActive\":true}}]";
        Resource resource = new ByteArrayResource(json.getBytes());
        dataMap.put("TraineeResource", resource);
        builderMap.put("TraineeBuilder", new TraineeBuilder(objectMapper));
        when(objectMapperProvider.getObject()).thenReturn(objectMapper);

        Map<Long, GymEntity> storage = new HashMap<>();
        dataPopulator.postProcessBeforeInitialization(storage, "TraineeStorage");

        assertEquals(1, storage.size());
        assertTrue(storage.containsKey(1L));
    }

    @Test
    void testPostProcessBeforeInitializationReturnsTheSameMapBean() throws Exception {
        String json = "[]";
        Resource resource = new ByteArrayResource(json.getBytes());
        dataMap.put("TraineeResource", resource);
        builderMap.put("TraineeBuilder", new TraineeBuilder(objectMapper));
        when(objectMapperProvider.getObject()).thenReturn(objectMapper);

        Map<Long, GymEntity> storage = new HashMap<>();
        Object result = dataPopulator.postProcessBeforeInitialization(storage, "TraineeStorage");

        assertSame(storage, result);
    }

    @Test
    void testPostProcessBeforeInitializationThrowsWhenBuilderNotFound() {
        String json = "[{\"traineePK\":1}]";
        Resource resource = new ByteArrayResource(json.getBytes());
        dataMap.put("TraineeResource", resource);
        when(objectMapperProvider.getObject()).thenReturn(objectMapper);

        Map<Long, GymEntity> storage = new HashMap<>();

        assertThrows(RuntimeException.class,
                () -> dataPopulator.postProcessBeforeInitialization(storage, "TraineeStorage"));
    }

    @Test
    void testPostProcessBeforeInitializationPopulatesMultipleEntities() throws Exception {
        String json = "[" +
                "{\"traineePK\":1,\"dateOfBirth\":null,\"address\":\"A1\",\"user\":null}," +
                "{\"traineePK\":2,\"dateOfBirth\":null,\"address\":\"A2\",\"user\":null}" +
                "]";
        Resource resource = new ByteArrayResource(json.getBytes());
        dataMap.put("TraineeResource", resource);
        builderMap.put("TraineeBuilder", new TraineeBuilder(objectMapper));
        when(objectMapperProvider.getObject()).thenReturn(objectMapper);

        Map<Long, GymEntity> storage = new HashMap<>();
        dataPopulator.postProcessBeforeInitialization(storage, "TraineeStorage");

        assertEquals(2, storage.size());
        assertTrue(storage.containsKey(1L));
        assertTrue(storage.containsKey(2L));
    }
}
