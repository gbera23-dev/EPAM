package app.integration.cucumber;

import app.domain.entities.Trainee;
import app.domain.entities.Trainer;
import app.domain.entities.Training;
import app.domain.entities.TrainingType;
import app.domain.exceptions.TrainingNotFoundException;
import app.domain.exceptions.TrainingTypeNotFoundException;
import app.domain.exceptions.UserNotFoundException;
import app.domain.persistence.GymRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RequiredArgsConstructor
public class TrainingManagementSteps {

    private final GymRepository gymRepository;
    private final MockMvc mockMvc;
    private final TestContext testContext;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;

    @Given("a training named {string} exists for trainee {string} with trainer {string} on {string} lasting {int}")
    public void a_training_named_exists_for_trainee_with_trainer_on_lasting(String name,
                                                                            String traineeUsername,
                                                                            String trainerUsername,
                                                                            String date,
                                                                            Integer duration) {
        testContext.setCurrentTrainingId(transactionTemplate.execute(status -> {
            Trainee trainee = gymRepository.getTraineeRepository()
                    .findByUserUsername(traineeUsername)
                    .orElseThrow(() -> new UserNotFoundException(
                            "Fixture failed: no trainee " + traineeUsername));

            Trainer trainer = gymRepository.getTrainerRepository()
                    .findByUserUsername(trainerUsername)
                    .orElseThrow(() -> new UserNotFoundException(
                            "Fixture failed: no trainer " + trainerUsername));

            Training training = new Training();
            training.setName(name);
            training.setDate(LocalDate.parse(date));
            training.setDuration(duration);
            training.setTrainee(trainee);
            training.setTrainer(trainer);
            training.setTrainingType(trainer.getTrainingType());

            return gymRepository.getTrainingRepository().save(training).getId();
        }));
    }

    @Given("the trainer {string} is updated with specialisation {string}")
    public void the_trainer_is_updated_with_specialisation(String trainerUsername, String trainingTypeName)
            throws Exception {
        Trainer trainer = gymRepository.getTrainerRepository().findByUserUsername(trainerUsername)
                .orElseThrow(() -> new UserNotFoundException(
                        "Fixture failed: no trainer " + trainerUsername));

        ObjectNode body = objectMapper.createObjectNode();
        body.put("username", trainerUsername);
        body.put("firstName", trainer.getUser().getFirstName());
        body.put("lastName", trainer.getUser().getLastName());
        body.put("isActive", trainer.getUser().isActive());
        body.putObject("specialization").put("trainingTypeId", trainingTypeIdOf(trainingTypeName));

        perform(put("/api/trainer/update"), body);

        assertThat(testContext.getStatus())
                .as("trainer specialisation update during fixture setup")
                .isEqualTo(HttpStatus.OK);
    }

    @Given("the trainers of {string} are {string}")
    public void the_trainers_of_are(String traineeUsername, String trainerUsernames) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("traineeUsername", traineeUsername);
        ArrayNode usernames = body.putArray("trainerUsernames");
        for (String username : trainerUsernames.split("\\s*,\\s*")) {
            usernames.add(username);
        }

        perform(put("/api/trainee/update-trainers"), body);

        assertThat(testContext.getStatus())
                .as("trainer assignment during fixture setup")
                .isEqualTo(HttpStatus.OK);
    }


    @When("a training named {string} is added for trainee {string} with trainer {string} on {string} lasting {int}")
    public void a_training_named_is_added_for_trainee_with_trainer_on_lasting(String name,
                                                                              String traineeUsername,
                                                                              String trainerUsername,
                                                                              String date,
                                                                              Integer duration) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("traineeUsername", traineeUsername);
        body.put("trainerUsername", trainerUsername);
        body.put("trainingName", name.isBlank() ? null : name);
        body.put("date", date.isBlank() ? null : date);
        body.put("duration", duration);

        perform(post("/api/trainings"), body);
        if(testContext.getStatus().is2xxSuccessful()) {
            testContext.setCurrentTrainingId(trainingByName(name).getId());
        }
    }

    @When("that training is deleted")
    public void that_training_is_deleted() throws Exception {
        assertThat(testContext.getCurrentTrainingId())
                .as("no training has been created or looked up yet")
                .isNotNull();
        deleteTraining(testContext.getCurrentTrainingId());
    }

    @When("the training named {string} is deleted")
    public void the_training_named_is_deleted(String name) throws Exception {
        deleteTraining(trainingByName(name).getId());
    }

    @When("the training with id {int} is deleted")
    public void the_training_with_id_is_deleted(Integer trainingId) throws Exception {
        deleteTraining(trainingId.longValue());
    }

    @When("the trainee {string} is deleted")
    public void the_trainee_is_deleted(String username) throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete("/api/trainee")
                        .param("username", username)
                        .header("Authorization", "Bearer " + testContext.getToken()))
                .andReturn();
        captureResponse(mvcResult);
    }


    @Then("a training named {string} should exist for trainee {string}")
    public void a_training_named_should_exist_for_trainee(String name, String traineeUsername) {
        Training training = trainingByName(name);
        assertThat(training.getTrainee().getUser().getUsername()).isEqualTo(traineeUsername);
        testContext.setCurrentTrainingId(training.getId());
    }

    @Then("no training named {string} should exist")
    public void no_training_named_should_exist(String name) {
        assertThat(trainingNames()).doesNotContain(name);
    }

    @Then("that training should have trainer {string}")
    public void that_training_should_have_trainer(String trainerUsername) {
        assertThat(currentTraining().getTrainer().getUser().getUsername()).isEqualTo(trainerUsername);
    }

    @Then("that training should be on {string} with duration {int}")
    public void that_training_should_be_on_with_duration(String date, Integer duration) {
        Training training = currentTraining();
        assertThat(training.getDate()).isEqualTo(LocalDate.parse(date));
        assertThat(training.getDuration()).isEqualTo(duration);
    }

    @Then("that training should have training type {string}")
    public void that_training_should_have_training_type(String trainingTypeName) {
        assertThat(currentTraining().getTrainingType().getName()).isEqualTo(trainingTypeName);
    }

    @Then("no trainee profile should exist for {string}")
    public void no_trainee_profile_should_exist_for(String username) {
        assertThat(gymRepository.getTraineeRepository().findByUserUsername(username)).isEmpty();
    }

    @Then("no trainings should exist for trainee {string}")
    public void no_trainings_should_exist_for_trainee(String traineeUsername) {
        List<String> remaining = gymRepository.getTrainingRepository().findAll().stream()
                .filter(t -> t.getTrainee() != null
                        && traineeUsername.equals(t.getTrainee().getUser().getUsername()))
                .map(Training::getName)
                .toList();
        assertThat(remaining).isEmpty();
    }

    @Then("the trainees of {string} should not include {string}")
    public void the_trainees_of_should_not_include(String trainerUsername, String traineeUsername) {
        List<String> trainees = transactionTemplate.execute(status ->
                gymRepository.getTrainerRepository().findByUserUsername(trainerUsername)
                        .orElseThrow(() -> new UserNotFoundException(
                                "No trainer " + trainerUsername))
                        .getTrainees().stream()
                        .map(trainee -> trainee.getUser().getUsername())
                        .toList());

        assertThat(trainees).doesNotContain(traineeUsername);
    }

    private void deleteTraining(Long trainingId) throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete("/api/trainings/{id}", trainingId)
                        .header("Authorization", "Bearer " + testContext.getToken()))
                .andReturn();
        captureResponse(mvcResult);
    }

    private void perform(MockHttpServletRequestBuilder request, ObjectNode body) throws Exception {
        MvcResult mvcResult = mockMvc.perform(request
                        .header("Authorization", "Bearer " + testContext.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();
        captureResponse(mvcResult);
    }

    private void captureResponse(MvcResult mvcResult) throws Exception {
        testContext.setStatus(HttpStatus.valueOf(mvcResult.getResponse().getStatus()));
        testContext.setResponseBody(mvcResult.getResponse().getContentAsString());
    }

    private Training currentTraining() {
        assertThat(testContext.getCurrentTrainingId())
                .as("no training has been created or looked up yet")
                .isNotNull();
        return gymRepository.getTrainingRepository().findById(testContext.getCurrentTrainingId())
                .orElseThrow(() -> new TrainingNotFoundException(
                        "Training " + testContext.getCurrentTrainingId() + " no longer exists"));
    }

    private Training trainingByName(String name) {
        return gymRepository.getTrainingRepository().findAll().stream()
                .filter(training -> name.equals(training.getName()))
                .findFirst()
                .orElseThrow(() -> new TrainingNotFoundException("No training named " + name));
    }

    private List<String> trainingNames() {
        return gymRepository.getTrainingRepository().findAll().stream()
                .map(Training::getName)
                .toList();
    }

    private Long trainingTypeIdOf(String trainingTypeName) {
        return gymRepository.getTrainingTypeRepository().findAll().stream()
                .filter(type -> trainingTypeName.equals(type.getName()))
                .map(TrainingType::getId)
                .findFirst()
                .orElseThrow(() -> new TrainingTypeNotFoundException(
                        "No training type named " + trainingTypeName));
    }
}