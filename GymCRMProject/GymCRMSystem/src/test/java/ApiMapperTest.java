import app.dto.api.request.TraineeRegistrationRequest;
import app.dto.api.request.TrainerRegistrationRequest;
import app.dto.api.response.*;
import app.entities.*;
import app.mappers.api.TraineeApiMapper;
import app.mappers.api.TrainerApiMapper;
import app.mappers.api.TrainingApiMapper;
import app.mappers.api.TrainingTypeApiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ApiMapperTest {

    TrainingTypeApiMapper trainingTypeApiMapper;
    TrainerApiMapper trainerApiMapper;
    TraineeApiMapper traineeApiMapper;
    TrainingApiMapper trainingApiMapper;

    @BeforeEach
    public void setup() {
        trainingTypeApiMapper = new TrainingTypeApiMapper();
        trainerApiMapper = new TrainerApiMapper();
        traineeApiMapper = new TraineeApiMapper(trainerApiMapper);
        trainingApiMapper = new TrainingApiMapper();
    }

    private User buildUser(String firstName, String lastName, String username, boolean active) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setActive(active);
        return user;
    }

    private TrainingType buildTrainingType(Long id, String name) {
        TrainingType trainingType = new TrainingType();
        trainingType.setId(id);
        trainingType.setName(name);
        return trainingType;
    }

    @Test
    public void toTrainingTypeResponseTest() {
        TrainingType trainingType = buildTrainingType(1L, "Yoga");

        TrainingTypeResponse result = trainingTypeApiMapper.toTrainingTypeResponse(trainingType);

        assertEquals("Yoga", result.getTrainingTypeName());
        assertEquals(1L, result.getTrainingTypeId());
    }

    @Test
    public void toTrainerTest() {
        TrainerRegistrationRequest request = new TrainerRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        TrainingType trainingType = buildTrainingType(2L, "Pilates");

        Trainer result = trainerApiMapper.toTrainer(request, trainingType);

        assertEquals("John", result.getUser().getFirstName());
        assertEquals("Doe", result.getUser().getLastName());
        assertEquals(trainingType, result.getTrainingType());
    }

    @Test
    public void toTrainerProfileResponseTest() {
        TrainingType trainingType = buildTrainingType(3L, "CrossFit");
        User trainerUser = buildUser("Alice", "Smith", "alice.smith", true);
        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setTrainingType(trainingType);

        Trainee trainee = new Trainee();
        trainee.setUser(buildUser("Bob", "Jones", "bob.jones", true));
        trainer.setTrainees(List.of(trainee));

        TrainerProfileResponse result = trainerApiMapper.toTrainerProfileResponse(trainer);

        assertEquals("Alice", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("CrossFit", result.getSpecialization().getTrainingTypeName());
        assertEquals(3L, result.getSpecialization().getTrainingTypeId());
        assertTrue(result.isActive());
        assertEquals(1, result.getTraineeSummaryResponseList().size());
        assertEquals("bob.jones", result.getTraineeSummaryResponseList().get(0).getUsername());
        assertEquals("Bob", result.getTraineeSummaryResponseList().get(0).getFirstName());
        assertEquals("Jones", result.getTraineeSummaryResponseList().get(0).getLastName());
    }

    @Test
    public void toTrainerSummaryResponseTest() {
        TrainingType trainingType = buildTrainingType(4L, "Boxing");
        User trainerUser = buildUser("Carol", "White", "carol.white", true);
        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setTrainingType(trainingType);

        TrainerSummaryResponse result = trainerApiMapper.toTrainerSummaryResponse(trainer);

        assertEquals("carol.white", result.getUsername());
        assertEquals("Carol", result.getFirstName());
        assertEquals("White", result.getLastName());
        assertEquals("Boxing", result.getSpecialization().getTrainingTypeName());
        assertEquals(4L, result.getSpecialization().getTrainingTypeId());
    }

    @Test
    public void toTraineeTest() {
        TraineeRegistrationRequest request = new TraineeRegistrationRequest();
        request.setFirstName("Dave");
        request.setLastName("Brown");
        request.setAddress("123 Main St");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));

        Trainee result = traineeApiMapper.toTrainee(request);

        assertEquals("Dave", result.getUser().getFirstName());
        assertEquals("Brown", result.getUser().getLastName());
        assertEquals("123 Main St", result.getAddress());
        assertEquals(LocalDate.of(1990, 5, 15), result.getDateOfBirth());
    }

    @Test
    public void toTraineeProfileResponseTest() {
        TrainingType trainingType = buildTrainingType(5L, "Swimming");
        User trainerUser = buildUser("Eve", "Davis", "eve.davis", true);
        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setTrainingType(trainingType);

        User traineeUser = buildUser("Frank", "Miller", "frank.miller", true);
        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);
        trainee.setAddress("456 Oak Ave");
        trainee.setDateOfBirth(LocalDate.of(1995, 8, 20));
        trainee.setTrainers(List.of(trainer));

        TraineeProfileResponse result = traineeApiMapper.toTraineeProfileResponse(trainee);

        assertEquals("Frank", result.getFirstName());
        assertEquals("Miller", result.getLastName());
        assertEquals(LocalDate.of(1995, 8, 20), result.getDateOfBirth());
        assertEquals("456 Oak Ave", result.getAddress());
        assertTrue(result.isActive());
        assertEquals(1, result.getTrainerList().size());
        assertEquals("eve.davis", result.getTrainerList().get(0).getUsername());
        assertEquals("Eve", result.getTrainerList().get(0).getFirstName());
        assertEquals("Davis", result.getTrainerList().get(0).getLastName());
        assertEquals("Swimming", result.getTrainerList().get(0).getSpecialization().getTrainingTypeName());
    }

    @Test
    public void toTrainingResponseTest() {
        TrainingType trainingType = buildTrainingType(6L, "Cycling");
        User trainerUser = buildUser("Grace", "Lee", "grace.lee", true);
        Trainer trainer = new Trainer();
        trainer.setUser(trainerUser);
        trainer.setTrainingType(trainingType);

        Training training = new Training();
        training.setName("Morning Ride");
        training.setDate(LocalDate.of(2024, 3, 10));
        training.setDuration(60);
        training.setTrainingType(trainingType);
        training.setTrainer(trainer);

        TrainingResponse result = trainingApiMapper.toTrainingResponse(training);

        assertEquals("Morning Ride", result.getTrainingName());
        assertEquals("Cycling", result.getTrainingTypeResponse().getTrainingTypeName());
        assertEquals(6L, result.getTrainingTypeResponse().getTrainingTypeId());
        assertEquals(60, result.getDuration());
        assertEquals("Grace Lee", result.getTrainerName());
    }
}
