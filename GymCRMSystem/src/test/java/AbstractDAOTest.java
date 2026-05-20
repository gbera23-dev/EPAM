import entities.Trainee;
import entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.TraineeDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AbstractDAOTest {

    private TraineeDAO traineeDAO;
    private Map<Long, Trainee> storage;

    private Trainee createTrainee(long pk) {
        return new Trainee(pk, null, "Address " + pk, new User(pk, "First", "Last", "user" + pk, "pass", true));
    }

    @BeforeEach
    void setUp() {
        traineeDAO = new TraineeDAO();
        storage = new HashMap<>();
        traineeDAO.setStorage(storage);
    }

    @Test
    void testGetEntityReturnsCorrectEntity() {
        Trainee trainee = createTrainee(1L);
        storage.put(1L, trainee);

        Trainee result = traineeDAO.getEntity(1L);

        assertEquals(trainee, result);
    }

    @Test
    void testGetEntityReturnsNullForMissingKey() {
        Trainee result = traineeDAO.getEntity(999L);

        assertNull(result);
    }

    @Test
    void testSavePersistsEntityInStorage() {
        Trainee trainee = createTrainee(2L);

        traineeDAO.save(2L, trainee);

        assertEquals(trainee, storage.get(2L));
    }

    @Test
    void testSaveOverwritesExistingEntity() {
        Trainee original = createTrainee(3L);
        Trainee updated = createTrainee(3L);
        storage.put(3L, original);

        traineeDAO.save(3L, updated);

        assertEquals(updated, storage.get(3L));
    }

    @Test
    void testDeleteRemovesEntityFromStorage() {
        Trainee trainee = createTrainee(4L);
        storage.put(4L, trainee);

        traineeDAO.delete(4L);

        assertFalse(storage.containsKey(4L));
    }

    @Test
    void testDeleteNonExistentKeyDoesNotThrow() {
        assertDoesNotThrow(() -> traineeDAO.delete(888L));
    }

    @Test
    void testGetAllReturnsAllEntities() {
        storage.put(1L, createTrainee(1L));
        storage.put(2L, createTrainee(2L));
        storage.put(3L, createTrainee(3L));

        List<Trainee> result = traineeDAO.getAll();

        assertEquals(3, result.size());
    }

    @Test
    void testGetAllReturnsEmptyListWhenStorageIsEmpty() {
        List<Trainee> result = traineeDAO.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void testSetStorageReplacesExistingStorage() {
        storage.put(1L, createTrainee(1L));
        Map<Long, Trainee> newStorage = new HashMap<>();
        newStorage.put(2L, createTrainee(2L));

        traineeDAO.setStorage(newStorage);

        assertNull(traineeDAO.getEntity(1L));
        assertNotNull(traineeDAO.getEntity(2L));
    }
}
