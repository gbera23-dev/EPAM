import entities.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraineeTest {

    private Trainee createTrainee() {
        User user = new User(1L, "John", "Doe", "john.doe", "pass", true, null, null);
        return new Trainee(10L, LocalDate.now(), "123 Main St", user, null, null);
    }

    @Test
    void testGetEntityIdReturnsTraineePK() {
        Trainee trainee = createTrainee();

        assertEquals(10L, trainee.getEntityId());
    }

    @Test
    void testGetTraineePKReturnsCorrectValue() {
        Trainee trainee = createTrainee();

        assertEquals(10L, trainee.getId());
    }

    @Test
    void testSetTraineePKUpdatesValue() {
        Trainee trainee = createTrainee();

        trainee.setId(99L);

        assertEquals(99L, trainee.getId());
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
        LocalDate dob = LocalDate.now();
        Trainee trainee = new Trainee(1L, dob, "addr", null, null, null);

        assertEquals(dob, trainee.getDateOfBirth());
    }

    @Test
    void testSetDateOfBirthUpdatesValue() {
        Trainee trainee = createTrainee();
        LocalDate newDate = LocalDate.now();

        trainee.setDateOfBirth(newDate);

        assertEquals(newDate, trainee.getDateOfBirth());
    }

    @Test
    void testGetUserReturnsCorrectUser() {
        User user = new User(1L, "John", "Doe", "john.doe", "pass", true, null, null);
        Trainee trainee = new Trainee(1L, null, "addr", user, null, null);

        assertEquals(user, trainee.getUser());
    }

    @Test
    void testSetUserIdUpdatesUser() {
        Trainee trainee = createTrainee();
        User newUser = new User(2L, "Jane", "Smith", "jane.smith", "pw", false, null, null);

        trainee.setUser(newUser);

        assertEquals(newUser, trainee.getUser());
    }

}

class TrainerTest {

    private Trainer createTrainer() {
        User user = new User(2L, "Alice", "Wonder", "alice.wonder", "pw123", true, null, null);
        TrainingType trainingType = new TrainingType();
        trainingType.setName("Pilates");
        return new Trainer(20L, trainingType, user, null, null);
    }

    @Test
    void testGetEntityIdReturnsTrainerPK() {
        Trainer trainer = createTrainer();

        assertEquals(20L, trainer.getEntityId());
    }

    @Test
    void testgetTrainerPkReturnsCorrectValue() {
        Trainer trainer = createTrainer();

        assertEquals(20L, trainer.getId());
    }

    @Test
    void testSetTrainerPKUpdatesValue() {
        Trainer trainer = createTrainer();

        trainer.setId(55L);

        assertEquals(55L, trainer.getId());
    }

    @Test
    void testGetSpecializationReturnsCorrectValue() {
        Trainer trainer = createTrainer();

        assertEquals("Pilates", trainer.getTrainingType().getName());
    }

    @Test
    void testSetSpecializationUpdatesValue() {
        Trainer trainer = createTrainer();
        TrainingType trainingType = new TrainingType();
        trainingType.setName("CrossFit");
        trainer.setTrainingType(trainingType);

        assertEquals("CrossFit", trainer.getTrainingType().getName());
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
        User newUser = new User(3L, "Bob", "Green", "bob.green", "pw", true, null, null);

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
        Trainer trainer = new Trainer();
        Trainee trainee = new Trainee();
        trainee.setId(5L);
        trainer.setId(8L);

        return new Training(30L, trainee,trainer, "Morning Jog", type,
                LocalDate.now(), 45);
    }

    @Test
    void testGetEntityIdReturnsTrainingPK() {
        Training training = createTraining();

        assertEquals(30L, training.getEntityId());
    }

    @Test
    void testGetTrainingPkReturnsCorrectValue() {
        Training training = createTraining();

        assertEquals(30L, training.getId());
    }

    @Test
    void testSetTrainingPKUpdatesValue() {
        Training training = createTraining();

        training.setId(88L);

        assertEquals(88L, training.getId());
    }

    @Test
    void testGetTraineeIdReturnsCorrectValue() {
        Training training = createTraining();

        assertEquals(5L, training.getTrainee().getId());
    }

    @Test
    void testSetTraineeIdUpdatesValue() {
        Training training = createTraining();
        Trainee trainee = new Trainee();

        trainee.setId(99L);

        training.setTrainee(trainee);

        assertEquals(99L, training.getTrainee().getId());
    }

    @Test
    void testGetTrainerIdReturnsCorrectValue() {
        Training training = createTraining();

        assertEquals(8L, training.getTrainer().getId());
    }

    @Test
    void testSetTrainerIdUpdatesValue() {
        Training training = createTraining();
        Trainer trainer = new Trainer();
        trainer.setId(77L);
        training.setTrainer(trainer);

        assertEquals(77L, training.getTrainer().getId());
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
        LocalDate date = LocalDate.now();
        Training training = new Training(1L, null, null, "test", null, date, 10);

        assertEquals(date, training.getDate());
    }

    @Test
    void testSetDateUpdatesValue() {
        Training training = createTraining();
        LocalDate newDate = LocalDate.now();

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
        return new User(1L, "John", "Doe", "john.doe", "password123", true, null, null);
    }

    @Test
    void testGetUserIdReturnsCorrectValue() {
        User user = createUser();

        assertEquals(1L, user.getId());
    }

    @Test
    void testSetUserIdUpdatesValue() {
        User user = createUser();

        user.setId(42L);

        assertEquals(42L, user.getId());
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

        assertTrue(user.isActive());
    }

    @Test
    void testSetIsActiveUpdatesToFalse() {
        User user = createUser();

        user.setActive(false);

        assertFalse(user.isActive());
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
