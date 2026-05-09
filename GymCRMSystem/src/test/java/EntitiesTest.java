import entities.*;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TraineeTest {

    private Trainee createTrainee() {
        User user = new User(1L, "John", "Doe", "john.doe", "pass", true);
        return new Trainee(10L, new Date(), "123 Main St", user);
    }

    @Test
    void testGetEntityIdReturnsTraineePK() {
        Trainee trainee = createTrainee();

        assertEquals(10L, trainee.getEntityId());
    }

    @Test
    void testGetTraineePKReturnsCorrectValue() {
        Trainee trainee = createTrainee();

        assertEquals(10L, trainee.getTraineePK());
    }

    @Test
    void testSetTraineePKUpdatesValue() {
        Trainee trainee = createTrainee();

        trainee.setTraineePK(99L);

        assertEquals(99L, trainee.getTraineePK());
    }

    @Test
    void testGetAddressReturnsCorrectValue() {
        Trainee trainee = createTrainee();

        assertEquals("123 Main St", trainee.getAddress());
    }

    @Test
    void testSetAddressUpdatesValue() {
        Trainee trainee = createTrainee();

        trainee.setAddress("456 Elm Ave");

        assertEquals("456 Elm Ave", trainee.getAddress());
    }

    @Test
    void testGetDateOfBirthReturnsCorrectValue() {
        Date dob = new Date();
        Trainee trainee = new Trainee(1L, dob, "addr", null);

        assertEquals(dob, trainee.getDateOfBirth());
    }

    @Test
    void testSetDateOfBirthUpdatesValue() {
        Trainee trainee = createTrainee();
        Date newDate = new Date(0);

        trainee.setDateOfBirth(newDate);

        assertEquals(newDate, trainee.getDateOfBirth());
    }

    @Test
    void testGetUserReturnsCorrectUser() {
        User user = new User(1L, "John", "Doe", "john.doe", "pass", true);
        Trainee trainee = new Trainee(1L, null, "addr", user);

        assertEquals(user, trainee.getUser());
    }

    @Test
    void testSetUserIdUpdatesUser() {
        Trainee trainee = createTrainee();
        User newUser = new User(2L, "Jane", "Smith", "jane.smith", "pw", false);

        trainee.setUserId(newUser);

        assertEquals(newUser, trainee.getUser());
    }

    @Test
    void testToStringContainsTraineePK() {
        Trainee trainee = createTrainee();

        assertTrue(trainee.toString().contains("10"));
    }
}

class TrainerTest {

    private Trainer createTrainer() {
        User user = new User(2L, "Alice", "Wonder", "alice.wonder", "pw123", true);
        return new Trainer(20L, "Pilates", user);
    }

    @Test
    void testGetEntityIdReturnsTrainerPK() {
        Trainer trainer = createTrainer();

        assertEquals(20L, trainer.getEntityId());
    }

    @Test
    void testGetTrainerPKReturnsCorrectValue() {
        Trainer trainer = createTrainer();

        assertEquals(20L, trainer.getTrainerPK());
    }

    @Test
    void testSetTrainerPKUpdatesValue() {
        Trainer trainer = createTrainer();

        trainer.setTrainerPK(55L);

        assertEquals(55L, trainer.getTrainerPK());
    }

    @Test
    void testGetSpecializationReturnsCorrectValue() {
        Trainer trainer = createTrainer();

        assertEquals("Pilates", trainer.getSpecialization());
    }

    @Test
    void testSetSpecializationUpdatesValue() {
        Trainer trainer = createTrainer();

        trainer.setSpecialization("CrossFit");

        assertEquals("CrossFit", trainer.getSpecialization());
    }

    @Test
    void testGetUserReturnsCorrectUser() {
        Trainer trainer = createTrainer();

        assertNotNull(trainer.getUser());
        assertEquals("Alice", trainer.getUser().getFirstName());
    }

    @Test
    void testSetUserUpdatesUser() {
        Trainer trainer = createTrainer();
        User newUser = new User(3L, "Bob", "Green", "bob.green", "pw", true);

        trainer.setUser(newUser);

        assertEquals(newUser, trainer.getUser());
    }

    @Test
    void testToStringContainsTrainerPK() {
        Trainer trainer = createTrainer();

        assertTrue(trainer.toString().contains("20"));
    }
}

class TrainingTest {

    private Training createTraining() {
        TrainingType type = new TrainingType();
        type.setId(1L);
        type.setName("Cardio");
        return new Training(30L, 5L, 8L, "Morning Jog", type, new Date(), 45);
    }

    @Test
    void testGetEntityIdReturnsTrainingPK() {
        Training training = createTraining();

        assertEquals(30L, training.getEntityId());
    }

    @Test
    void testGetTrainingPKReturnsCorrectValue() {
        Training training = createTraining();

        assertEquals(30L, training.getTrainingPK());
    }

    @Test
    void testSetTrainingPKUpdatesValue() {
        Training training = createTraining();

        training.setTrainingPK(88L);

        assertEquals(88L, training.getTrainingPK());
    }

    @Test
    void testGetTraineeIdReturnsCorrectValue() {
        Training training = createTraining();

        assertEquals(5L, training.getTraineeId());
    }

    @Test
    void testSetTraineeIdUpdatesValue() {
        Training training = createTraining();

        training.setTraineeId(99);

        assertEquals(99L, training.getTraineeId());
    }

    @Test
    void testGetTrainerIdReturnsCorrectValue() {
        Training training = createTraining();

        assertEquals(8L, training.getTrainerId());
    }

    @Test
    void testSetTrainerIdUpdatesValue() {
        Training training = createTraining();

        training.setTrainerId(77);

        assertEquals(77L, training.getTrainerId());
    }

    @Test
    void testGetNameReturnsCorrectValue() {
        Training training = createTraining();

        assertEquals("Morning Jog", training.getName());
    }

    @Test
    void testSetNameUpdatesValue() {
        Training training = createTraining();

        training.setName("Evening Run");

        assertEquals("Evening Run", training.getName());
    }

    @Test
    void testGetDurationReturnsCorrectValue() {
        Training training = createTraining();

        assertEquals(45, training.getDuration());
    }

    @Test
    void testSetDurationUpdatesValue() {
        Training training = createTraining();

        training.setDuration(90);

        assertEquals(90, training.getDuration());
    }

    @Test
    void testGetTrainingTypeReturnsCorrectValue() {
        Training training = createTraining();

        assertEquals("Cardio", training.getTrainingType().getName());
    }

    @Test
    void testSetTrainingTypeUpdatesValue() {
        Training training = createTraining();
        TrainingType newType = new TrainingType();
        newType.setName("Strength");

        training.setTrainingType(newType);

        assertEquals("Strength", training.getTrainingType().getName());
    }

    @Test
    void testGetDateReturnsCorrectValue() {
        Date date = new Date();
        Training training = new Training(1L, 1L, 1L, "test", null, date, 10);

        assertEquals(date, training.getDate());
    }

    @Test
    void testSetDateUpdatesValue() {
        Training training = createTraining();
        Date newDate = new Date(0);

        training.setDate(newDate);

        assertEquals(newDate, training.getDate());
    }

    @Test
    void testToStringContainsTrainingPK() {
        Training training = createTraining();

        assertTrue(training.toString().contains("30"));
    }
}

class TrainingTypeTest {

    @Test
    void testGetIdReturnsCorrectValue() {
        TrainingType type = new TrainingType();
        type.setId(5L);

        assertEquals(5L, type.getId());
    }

    @Test
    void testSetIdUpdatesValue() {
        TrainingType type = new TrainingType();

        type.setId(10L);

        assertEquals(10L, type.getId());
    }

    @Test
    void testGetNameReturnsCorrectValue() {
        TrainingType type = new TrainingType();
        type.setName("Yoga");

        assertEquals("Yoga", type.getName());
    }

    @Test
    void testSetNameUpdatesValue() {
        TrainingType type = new TrainingType();

        type.setName("Boxing");

        assertEquals("Boxing", type.getName());
    }
}

class UserTest {

    private User createUser() {
        return new User(1L, "John", "Doe", "john.doe", "password123", true);
    }

    @Test
    void testGetUserIdReturnsCorrectValue() {
        User user = createUser();

        assertEquals(1L, user.getUserId());
    }

    @Test
    void testSetUserIdUpdatesValue() {
        User user = createUser();

        user.setUserId(42L);

        assertEquals(42L, user.getUserId());
    }

    @Test
    void testGetFirstNameReturnsCorrectValue() {
        User user = createUser();

        assertEquals("John", user.getFirstName());
    }

    @Test
    void testSetFirstNameUpdatesValue() {
        User user = createUser();

        user.setFirstName("Jane");

        assertEquals("Jane", user.getFirstName());
    }

    @Test
    void testGetLastNameReturnsCorrectValue() {
        User user = createUser();

        assertEquals("Doe", user.getLastName());
    }

    @Test
    void testSetLastNameUpdatesValue() {
        User user = createUser();

        user.setLastName("Smith");

        assertEquals("Smith", user.getLastName());
    }

    @Test
    void testGetUsernameReturnsCorrectValue() {
        User user = createUser();

        assertEquals("john.doe", user.getUsername());
    }

    @Test
    void testSetUsernameUpdatesValue() {
        User user = createUser();

        user.setUsername("j.doe");

        assertEquals("j.doe", user.getUsername());
    }

    @Test
    void testGetPasswordReturnsCorrectValue() {
        User user = createUser();

        assertEquals("password123", user.getPassword());
    }

    @Test
    void testSetPasswordUpdatesValue() {
        User user = createUser();

        user.setPassword("newpass");

        assertEquals("newpass", user.getPassword());
    }

    @Test
    void testGetIsActiveReturnsTrueWhenActive() {
        User user = createUser();

        assertTrue(user.getIsActive());
    }

    @Test
    void testSetIsActiveUpdatesToFalse() {
        User user = createUser();

        user.setIsActive(false);

        assertFalse(user.getIsActive());
    }

    @Test
    void testDefaultConstructorCreatesUser() {
        User user = new User();

        assertNotNull(user);
    }

    @Test
    void testToStringContainsFirstName() {
        User user = createUser();

        assertTrue(user.toString().contains("John"));
    }
}
