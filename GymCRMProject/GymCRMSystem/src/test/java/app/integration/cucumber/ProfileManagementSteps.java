package app.integration.cucumber;

import app.domain.entities.Trainee;
import app.domain.entities.Trainer;
import app.domain.entities.User;
import app.domain.exceptions.UserNotFoundException;
import app.domain.persistence.GymRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@RequiredArgsConstructor
public class ProfileManagementSteps {

    private final GymRepository gymRepository;
    private final MockMvc mockMvc;
    private final TestContext testContext;
    private final ObjectMapper objectMapper;

    @Given("the profile {string} is active")
    public void the_profile_is_active(String username) {
        setActiveFlag(username, true);
    }

    @Given("the profile {string} is inactive")
    public void the_profile_is_inactive(String username) {
        setActiveFlag(username, false);
    }

    @Given("the trainee {string} has date of birth {string} and address {string}")
    public void the_trainee_has_date_of_birth_and_address(String username, String dateOfBirth, String address) {
        Trainee trainee = traineeOf(username);
        trainee.setDateOfBirth(TestUtils.toLocalDate(dateOfBirth));
        trainee.setAddress(address);
        gymRepository.getTraineeRepository().save(trainee);
    }

    @When("the {string} profile {string} is set to active")
    public void the_profile_is_set_to_active(String role, String username) throws Exception {
        changeStatus(role, username, true);
    }

    @When("the {string} profile {string} is set to inactive")
    public void the_profile_is_set_to_inactive(String role, String username) throws Exception {
        changeStatus(role, username, false);
    }

    @When("the trainee {string} is updated with the following data")
    public void the_trainee_is_updated_with_the_following_data(String username, List<Map<String, String>> rows)
            throws Exception {
        Map<String, String> row = rows.get(0);
        updateTrainee(username, row.get("firstName"), row.get("lastName"),
                row.get("dateOfBirth"), row.get("address"), row.get("isActive"));
    }

    @When("the trainee {string} is updated with first name {string} and last name {string} only")
    public void the_trainee_is_updated_with_first_name_and_last_name_only(String username,
                                                                          String firstName,
                                                                          String lastName) throws Exception {
        updateTrainee(username, firstName, lastName, null, null, null);
    }

    @When("the trainer {string} is updated without a specialisation")
    public void the_trainer_is_updated_without_a_specialisation(String trainerUsername) throws Exception {
        perform(put("/api/trainer/update"), trainerBody(trainerUsername));
    }

    @When("the trainer {string} is updated with training type id {int}")
    public void the_trainer_is_updated_with_training_type_id(String trainerUsername, Integer trainingTypeId)
            throws Exception {
        ObjectNode body = trainerBody(trainerUsername);
        body.putObject("specialization").put("trainingTypeId", trainingTypeId);

        perform(put("/api/trainer/update"), body);
    }

    @When("the trainee profile of {string} is requested")
    public void the_trainee_profile_of_is_requested(String username) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/trainee")
                        .param("username", username)
                        .header("Authorization", "Bearer " + testContext.getToken()))
                .andReturn();
        captureResponse(mvcResult);
    }

    @Then("the request should be rejected")
    public void the_request_should_be_rejected() {
        assertThat(testContext.getStatus().isError())
                .as("expected the request to be rejected but got %s", testContext.getStatus())
                .isTrue();
    }

    @Then("the profile {string} should be active")
    public void the_profile_should_be_active(String username) {
        assertThat(userOf(username).isActive()).isTrue();
    }

    @Then("the profile {string} should be inactive")
    public void the_profile_should_be_inactive(String username) {
        assertThat(userOf(username).isActive()).isFalse();
    }

    @Then("the trainee {string} should have first name {string} and last name {string}")
    public void the_trainee_should_have_first_name_and_last_name(String username,
                                                                 String firstName,
                                                                 String lastName) {
        User user = traineeOf(username).getUser();
        assertThat(user.getFirstName()).isEqualTo(firstName);
        assertThat(user.getLastName()).isEqualTo(lastName);
    }

    @Then("the trainee {string} should have date of birth {string}")
    public void the_trainee_should_have_date_of_birth(String username, String dateOfBirth) {
        assertThat(traineeOf(username).getDateOfBirth()).isEqualTo(TestUtils.toLocalDate(dateOfBirth));
    }

    @Then("the trainee {string} should have address {string}")
    public void the_trainee_should_have_address(String username, String address) {
        assertThat(traineeOf(username).getAddress()).isEqualTo(address);
    }

    @Then("the returned profile should have first name {string} and last name {string}")
    public void the_returned_profile_should_have_first_name_and_last_name(String firstName, String lastName)
            throws Exception {
        JsonNode root = objectMapper.readTree(testContext.getResponseBody());
        assertThat(root.get("firstName").textValue()).isEqualTo(firstName);
        assertThat(root.get("lastName").textValue()).isEqualTo(lastName);
    }

    private void changeStatus(String role, String username, boolean active) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        perform(patch("/api/{role}/{username}/status?active={active}", role, username,active),
                body);
    }

    private void updateTrainee(String username, String firstName, String lastName,
                               String dateOfBirth, String address, String isActive) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();

        body.put("username", username);
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.put("dateOfBirth", dateOfBirth);
        body.put("address", address);
        body.put("isActive", Boolean.parseBoolean(isActive));

        perform(put("/api/trainee/update"), body);
    }

    private ObjectNode trainerBody(String trainerUsername) {
        Trainer trainer = gymRepository.getTrainerRepository().findByUserUsername(trainerUsername)
                .orElseThrow(() -> new UserNotFoundException("No trainer " + trainerUsername));

        ObjectNode body = objectMapper.createObjectNode();
        body.put("username", trainerUsername);
        body.put("firstName", trainer.getUser().getFirstName());
        body.put("lastName", trainer.getUser().getLastName());
        body.put("isActive", trainer.getUser().isActive());
        return body;
    }

    private void setActiveFlag(String username, boolean active) {
        User user = userOf(username);
        user.setActive(active);
        gymRepository.getUserRepository().save(user);
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

    private User userOf(String username) {
        return gymRepository.getUserRepository().findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("No user " + username));
    }

    private Trainee traineeOf(String username) {
        return gymRepository.getTraineeRepository().findByUserUsername(username)
                .orElseThrow(() -> new UserNotFoundException("No trainee " + username));
    }
}