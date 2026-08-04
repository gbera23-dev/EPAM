package app.integration.cucumber;

import app.entities.Trainee;
import app.entities.Trainer;
import app.entities.Training;
import app.exceptions.UserNotFoundException;
import app.persistence.GymRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RequiredArgsConstructor
public class TrainingSearchSteps {

    private static final String KNOWN_PASSWORD = "test-password";

    private final GymRepository gymRepository;
    private final MockMvc mockMvc;
    private final TestContext testContext;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;

    @Given("the following trainings exist")
    public void the_following_trainings_exist(List<Map<String, String>> rows) {
        transactionTemplate.executeWithoutResult(status ->
                rows.forEach(row -> {
                    Trainee trainee = gymRepository.getTraineeRepository()
                            .findByUserUsername(row.get("trainee"))
                            .orElseThrow(() -> new UserNotFoundException(
                                    "Fixture failed: no trainee " + row.get("trainee")));

                    Trainer trainer = gymRepository.getTrainerRepository()
                            .findByUserUsername(row.get("trainer"))
                            .orElseThrow(() -> new UserNotFoundException(
                                    "Fixture failed: no trainer " + row.get("trainer")));

                    Training training = new Training();
                    training.setName(row.get("name"));
                    training.setDate(LocalDate.parse(row.get("date")));
                    training.setDuration(Integer.parseInt(row.get("duration")));
                    training.setTrainee(trainee);
                    training.setTrainer(trainer);
                    training.setTrainingType(trainer.getTrainingType());

                    gymRepository.getTrainingRepository().save(training);
                }));
    }

    @Given("{string} is logged in")
    public void is_logged_in(String username) throws Exception {
        app.entities.User user = gymRepository.getUserRepository().findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        "Fixture failed: no user " + username));
        user.setPassword(passwordEncoder.encode(KNOWN_PASSWORD));
        gymRepository.getUserRepository().save(user);

        MvcResult mvcResult = mockMvc.perform(get("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, KNOWN_PASSWORD)))
                .andReturn();

        assertThat(mvcResult.getResponse().getStatus())
                .as("login during fixture setup")
                .isEqualTo(HttpStatus.OK.value());

        testContext.setToken(objectMapper.readTree(mvcResult.getResponse().getContentAsString())
                .get("jwt-token").textValue());
    }

    @When("the trainings of trainee {string} are searched with no criteria")
    public void the_trainings_of_trainee_are_searched_with_no_criteria(String username)
            throws Exception {
        searchTraineeTrainings(username, null, null, null, null);
    }

    @When("the trainings of trainee {string} are searched from {string} to {string}")
    public void the_trainings_of_trainee_are_searched_from_to(String username, String from, String to)
            throws Exception {
        searchTraineeTrainings(username, from, to, null, null);
    }

    @When("the trainings of trainee {string} are searched with trainer name {string}")
    public void the_trainings_of_trainee_are_searched_with_trainer_name(String username, String trainerName)
            throws Exception {
        searchTraineeTrainings(username, null, null, trainerName, null);
    }

    @When("the trainings of trainee {string} are searched with training type {string}")
    public void the_trainings_of_trainee_are_searched_with_training_type(String username, String trainingTypeName)
            throws Exception {
        searchTraineeTrainings(username, null, null, null, trainingTypeName);
    }

    @When("the trainings of trainee {string} are searched with trainer name {string} from {string} to {string}")
    public void the_trainings_of_trainee_are_searched_with_trainer_name_from_to(String username,
                                                                                String trainerName,
                                                                                String from,
                                                                                String to) throws Exception {
        searchTraineeTrainings(username, from, to, trainerName, null);
    }

    @When("the trainings of trainer {string} are searched with no criteria")
    public void the_trainings_of_trainer_are_searched_with_no_criteria(String username) throws Exception {
        searchTrainerTrainings(username, null, null, null);
    }

    @When("the trainings of trainer {string} are searched with trainee name {string}")
    public void the_trainings_of_trainer_are_searched_with_trainee_name(String username, String traineeName)
            throws Exception {
        searchTrainerTrainings(username, null, null, traineeName);
    }

    @When("the trainings of trainer {string} are searched from {string} to {string}")
    public void the_trainings_of_trainer_are_searched_from_to(String username, String from, String to)
            throws Exception {
        searchTrainerTrainings(username, from, to, null);
    }

    @Then("the returned trainings should be {string}")
    public void the_returned_trainings_should_be(String expectedNames) throws Exception {
        List<String> expected = List.of(expectedNames.split("\\s*,\\s*"));
        assertThat(returnedTrainingNames()).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Then("the returned trainings should not include {string}")
    public void the_returned_trainings_should_not_include(String trainingName) throws Exception {
        assertThat(returnedTrainingNames()).doesNotContain(trainingName);
    }

    @Then("the returned trainings should be empty")
    public void the_returned_trainings_should_be_empty() throws Exception {
        assertThat(returnedTrainingNames()).isEmpty();
    }

    private void searchTraineeTrainings(String username, String from, String to,
                                        String trainerName, String trainingTypeName) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("username", username);
        body.put("from", from);
        body.put("to", to);
        body.put("trainerName", trainerName);
        // The controller dereferences getTrainingTypeRequest() without a null check,
        // so the nested object is always sent, with a null name when unfiltered.
        body.putObject("trainingTypeRequest").put("trainingTypeName", trainingTypeName);

        perform(get("/api/trainee/trainings"), body);
    }

    private void searchTrainerTrainings(String username, String from, String to,
                                        String traineeName) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("username", username);
        body.put("from", from);
        body.put("to", to);
        body.put("traineeName", traineeName);

        perform(get("/api/trainer/trainings"), body);
    }

    private void perform(MockHttpServletRequestBuilder request, ObjectNode body) throws Exception {
        MvcResult mvcResult = mockMvc.perform(request
                        .header("Authorization", "Bearer " + testContext.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();

        testContext.setStatus(HttpStatus.valueOf(mvcResult.getResponse().getStatus()));
        testContext.setResponseBody(mvcResult.getResponse().getContentAsString());
    }

    private List<String> returnedTrainingNames() throws Exception {
        JsonNode root = objectMapper.readTree(testContext.getResponseBody());
        assertThat(root.isArray())
                .as("expected a JSON array of trainings but got: %s", testContext.getResponseBody())
                .isTrue();

        List<String> names = new ArrayList<>();
        root.forEach(node -> names.add(trainingNameOf(node)));
        return names;
    }

    private String trainingNameOf(JsonNode node) {
        if (node.hasNonNull("trainingName")) {
            return node.get("trainingName").textValue();
        }
        if (node.hasNonNull("name")) {
            return node.get("name").textValue();
        }
        throw new IllegalStateException(
                "TrainingResponse has no trainingName/name field, got: " + node);
    }
}