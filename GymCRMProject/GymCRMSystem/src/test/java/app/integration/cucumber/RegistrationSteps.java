package app.integration.cucumber;

import app.entities.Trainee;
import app.entities.Trainer;
import app.entities.TrainingType;
import app.entities.User;
import app.exceptions.UserNotFoundException;
import app.persistence.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RequiredArgsConstructor
public class RegistrationSteps {

    private final GymRepository gymRepository;
    private final MockMvc mockMvc;
    private final TestContext testContext;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    @Given("the database is empty")
    public void the_database_is_empty() {
        gymRepository.getTrainingRepository().deleteAllInBatch();
        gymRepository.getTraineeRepository().deleteAllInBatch();
        gymRepository.getTrainerRepository().deleteAllInBatch();
        gymRepository.getTrainingTypeRepository().deleteAllInBatch();
        gymRepository.getUserRepository().deleteAllInBatch();
        gymRepository.getJwtRepository().deleteAllInBatch();
        jdbcTemplate.execute("DELETE FROM TRAINER_TRAINEE");
    }

    @Given("a training type {string} exists")
    public void a_training_type_exists(String trainingTypeName) {
        TrainingType trainingType = new TrainingType(null, trainingTypeName, null, null);
        gymRepository.getTrainingTypeRepository().save(trainingType);
    }

    @When("a trainee is registered with first name {string} and last name {string}")
    public void a_trainee_is_registered_with_first_name_and_last_name(String firstName, String lastName)
            throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/trainee/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                   {
                     "firstName": "%s",
                     "lastName": "%s",
                     "dateOfBirth": "1990-01-15",
                     "address": "123 Main St"
                   }
                 """.formatted(firstName, lastName)))
                .andReturn();

        testContext.setStatus(HttpStatus.valueOf(mvcResult.getResponse().getStatus()));
        testContext.setResponseBody(mvcResult.getResponse().getContentAsString());
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(Integer status) {
        assertThat(testContext.getStatus()).isEqualTo(HttpStatus.valueOf(status));
    }

    @Then("the returned username should be {string}")
    public void the_returned_username_should_be(String username) throws JsonProcessingException {
        String jsonBody = testContext.getResponseBody();
        JsonNode jsonNode = objectMapper.readTree(jsonBody);
        String returnedUsername = jsonNode.get("username").textValue();
        System.out.println("expected: " + username +  " , actual: "+returnedUsername );
        assertThat(returnedUsername).isEqualTo(username);
    }

    @Then("the response should contain a generated password")
    public void the_response_should_contain_a_generated_password() throws JsonProcessingException {
        String jsonBody = testContext.getResponseBody();
        JsonNode jsonNode = objectMapper.readTree(jsonBody);
        String generatedPassword = objectMapper.readTree(jsonBody).get("password").textValue();
        assertThat(generatedPassword).isNotNull().isNotEmpty();
    }

    @Then("a trainee profile should exist for {string}")
    public void a_trainee_profile_should_exist_for(String username) {
        TraineeRepository traineeRepository = gymRepository.getTraineeRepository();
        Optional<Trainee> optionalTrainee = traineeRepository.findByUserUsername(username);
        assertThat(optionalTrainee.isPresent()).isTrue();
    }

    @When("a trainer is registered with first name {string} and last name {string} specialising in {string}")
    public void a_trainer_is_registered_with_first_name_and_last_name_specialising_in
            (String firstName, String lastName, String trainingTypeName) throws Exception {
        TrainingTypeRepository trainingTypeRepository = gymRepository.getTrainingTypeRepository();

        TrainingType trainingType = trainingTypeRepository.save(new TrainingType(
                null, trainingTypeName, null, null));

        MvcResult mvcResult = mockMvc.perform(post("/api/trainer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {
                                  "firstName": "%s",
                                  "lastName": "%s",
                                  "specialization": { "trainingTypeId": %s }
                                }
                                """.formatted(firstName, lastName, trainingType.getId())))
                .andReturn();
        testContext.setStatus(HttpStatus.valueOf(mvcResult.getResponse().getStatus()));
        testContext.setResponseBody(mvcResult.getResponse().getContentAsString());
    }

    @Then("a trainer profile should exist for {string}")
    public void a_trainer_profile_should_exist_for(String username) {
        TrainerRepository trainerRepository = gymRepository.getTrainerRepository();
        Optional<Trainer> optionalTrainer = trainerRepository.findByUserUsername(username);
        assertThat(optionalTrainer.isPresent()).isTrue();
    }

    @Then("the trainer {string} should specialise in {string}")
    public void the_trainer_should_specialise_in(String trainerUsername, String trainingName) {
        TrainerRepository trainerRepository = gymRepository.getTrainerRepository();
        Optional<Trainer> optionalTrainer = trainerRepository.findByUserUsername(trainerUsername);
        assertThat(optionalTrainer.isPresent()).isTrue();
        assertThat(optionalTrainer.get().getTrainingType().getName()).isEqualTo(trainingName);
    }

    @Then("the stored password for {string} should not equal the returned password")
    public void the_stored_password_for_should_not_equal_the_returned_password(String username)
            throws JsonProcessingException {
        UserRepository userRepository = gymRepository.getUserRepository();
        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UserNotFoundException("Could not find user!")
                );

        String jsonBody = testContext.getResponseBody();
        JsonNode jsonNode = objectMapper.readTree(jsonBody);
        String returnedPassword = objectMapper.readTree(jsonBody).get("password").textValue();
        assertThat(returnedPassword).isNotEqualTo(user.getPassword());
    }

    @Then("the returned password should be accepted when logging in as {string}")
    public void the_returned_password_should_be_accepted_when_logging_in_as(String username)
            throws Exception {
        String jsonBody = testContext.getResponseBody();
        JsonNode jsonNode = objectMapper.readTree(jsonBody);
        String returnedPassword = objectMapper.readTree(jsonBody).get("password").textValue();

        MvcResult mvcResult = mockMvc.perform(get("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, returnedPassword))).andReturn();
        assertThat(HttpStatus.valueOf(mvcResult.getResponse().getStatus())).isEqualTo(HttpStatus.OK);
    }

    @When("a trainer is registered with first name {string} and last name {string} specialising in training type id {int}")
    public void a_trainer_is_registered_with_first_name_and_last_name_specialising_in_training_type_id
            (String firstName, String lastName, Integer trainingTypeId) throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/trainer/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {
                                  "firstName": "%s",
                                  "lastName": "%s",
                                  "specialization": { "trainingTypeId": "%s" }
                                }
                                """.formatted(firstName, lastName, trainingTypeId)))
                .andReturn();

        testContext.setStatus(HttpStatus.valueOf(mvcResult.getResponse().getStatus()));
        testContext.setResponseBody(mvcResult.getResponse().getContentAsString());
    }

    @Then("no trainer profile should exist for {string}")
    public void no_trainer_profile_should_exist_for(String username) {
        TrainerRepository trainerRepository = gymRepository.getTrainerRepository();
        Optional<Trainer> optionalTrainer = trainerRepository.findByUserUsername(username);
        assertThat(optionalTrainer.isEmpty()).isTrue();
    }

    @Then("no user should exist with first name {string} and last name {string}")
    public void no_user_should_exist_with_first_name_and_last_name(String firstName, String lastName) {
        UserRepository userRepository = gymRepository.getUserRepository();
        List<User> users = userRepository.findUsersByFirstNameAndLastName
                (firstName, lastName);
        assertThat(users).isNullOrEmpty();
    }
}
