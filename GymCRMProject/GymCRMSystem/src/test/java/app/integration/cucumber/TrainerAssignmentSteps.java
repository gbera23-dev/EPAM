package app.integration.cucumber;

import app.entities.Trainee;
import app.entities.Trainer;
import app.exceptions.UserNotFoundException;
import app.persistence.GymRepository;
import app.persistence.TraineeRepository;
import app.persistence.TrainerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@RequiredArgsConstructor
public class TrainerAssignmentSteps {

    private final GymRepository gymRepository;
    private final MockMvc mockMvc;
    private final TestContext testContext;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;

    @When("the trainers of {string} are set to {string}")
    public void the_trainers_of_are_set_to(String traineeUsername, String trainerUsernames) throws Exception {
        updateTrainers(traineeUsername, TestUtils.splitCommaSeparated(trainerUsernames));
    }

    @When("the trainers of {string} are set to an empty list")
    public void the_trainers_of_are_set_to_an_empty_list(String traineeUsername) throws Exception {
        updateTrainers(traineeUsername, List.of());
    }

    @When("the trainers not assigned to {string} are requested")
    public void the_trainers_not_assigned_to_are_requested(String traineeUsername) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/trainee/not-assigned-trainers")
                        .param("username", traineeUsername)
                        .header("Authorization", "Bearer " + testContext.getToken()))
                .andReturn();
        captureResponse(mvcResult);
    }

    @Then("the trainers assigned to {string} should be {string}")
    public void the_trainers_assigned_to_should_be(String traineeUsername, String expectedUsernames) {
        List<String> expected = TestUtils.splitCommaSeparated(expectedUsernames);
        assertThat(assignedTrainerUsernames(traineeUsername))
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Then("no trainers should be assigned to {string}")
    public void no_trainers_should_be_assigned_to(String traineeUsername) {
        assertThat(assignedTrainerUsernames(traineeUsername)).isEmpty();
    }

    @Then("{string} should not be assigned to {string}")
    public void should_not_be_assigned_to(String trainerUsername, String traineeUsername) {
        assertThat(assignedTrainerUsernames(traineeUsername)).doesNotContain(trainerUsername);
    }

    @Then("the trainees of {string} should include {string}")
    public void the_trainees_of_should_include(String trainerUsername, String traineeUsername) {
        assertThat(assignedTraineeUsernames(trainerUsername)).contains(traineeUsername);
    }

    @Then("the response should list exactly {int} trainers")
    public void the_response_should_list_exactly_trainers(Integer count) throws Exception {
        assertThat(returnedTrainerUsernames()).hasSize(count);
    }

    @Then("the returned trainers should be {string}")
    public void the_returned_trainers_should_be(String expectedUsernames) throws Exception {
        List<String> expected = TestUtils.splitCommaSeparated(expectedUsernames);
        assertThat(returnedTrainerUsernames()).containsExactlyInAnyOrderElementsOf(expected);
    }

    private void updateTrainers(String traineeUsername, List<String> trainerUsernames) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("traineeUsername", traineeUsername);
        ArrayNode usernames = body.putArray("trainerUsernames");
        trainerUsernames.forEach(usernames::add);

        perform(put("/api/trainee/update-trainers"), body);
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

    private List<String> assignedTrainerUsernames(String traineeUsername) {
        return transactionTemplate.execute(status ->
                gymRepository.getTraineeRepository().findByUserUsername(traineeUsername)
                        .orElseThrow(() -> new UserNotFoundException(
                                "No trainee " + traineeUsername))
                        .getTrainers().stream()
                        .map(trainer -> trainer.getUser().getUsername())
                        .toList());
    }

    private List<String> assignedTraineeUsernames(String trainerUsername) {
        return transactionTemplate.execute(status ->
                gymRepository.getTrainerRepository().findByUserUsername(trainerUsername)
                        .orElseThrow(() -> new UserNotFoundException(
                                "No trainer " + trainerUsername))
                        .getTrainees().stream()
                        .map(trainee -> trainee.getUser().getUsername())
                        .toList());
    }

    private List<String> returnedTrainerUsernames() throws Exception {
        JsonNode root = objectMapper.readTree(testContext.getResponseBody());
        assertThat(root.isArray())
                .as("expected a JSON array of trainers but got: %s", testContext.getResponseBody())
                .isTrue();

        List<String> usernames = new ArrayList<>();
        root.forEach(node -> usernames.add(trainerUsernameOf(node)));
        return usernames;
    }

    private String trainerUsernameOf(JsonNode node) {
        if (node.hasNonNull("username")) {
            return node.get("username").textValue();
        }
        if (node.hasNonNull("trainerUsername")) {
            return node.get("trainerUsername").textValue();
        }
        throw new IllegalStateException(
                "TrainerResponse has no username/trainerUsername field, got: " + node);
    }
}