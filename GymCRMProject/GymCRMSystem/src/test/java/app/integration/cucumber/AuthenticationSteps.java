package app.integration.cucumber;

import app.domain.entities.User;
import app.domain.exceptions.UserNotFoundException;
import app.domain.persistence.GymRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@RequiredArgsConstructor
public class AuthenticationSteps {

    private final GymRepository gymRepository;
    private final MockMvc mockMvc;
    private final TestContext testContext;
    private final ObjectMapper objectMapper;

    @Given("{string} has failed to log in {int} times")
    public void has_failed_to_log_in_times(String username, Integer attempts) throws Exception {
        failLogin(username, attempts);
    }

    @When("{string} fails to log in {int} more times")
    public void fails_to_log_in_more_times(String username, Integer attempts) throws Exception {
        failLogin(username, attempts);
    }

    @When("{string} logs in with the correct password")
    public void logs_in_with_the_correct_password(String username) throws Exception {
        login(username, currentPassword());
    }

    @When("{string} logs in with password {string}")
    public void logs_in_with_password(String username, String password) throws Exception {
        login(username, password);
    }

    @When("the trainee profile of {string} is requested without an authorization header")
    public void the_trainee_profile_of_is_requested_without_an_authorization_header(String username)
            throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/trainee")
                        .param("username", username))
                .andReturn();
        captureResponse(mvcResult);
    }

    @When("the trainee profile of {string} is requested with token {string}")
    public void the_trainee_profile_of_is_requested_with_token(String username, String token) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/trainee")
                        .param("username", username)
                        .header("Authorization", "Bearer " + token))
                .andReturn();
        captureResponse(mvcResult);
    }

    @When("{string} logs out")
    public void logs_out(String username) throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/user/logout")
                        .header("Authorization", "Bearer " + testContext.getToken()))
                .andReturn();
        captureResponse(mvcResult);
    }

    @When("a logout is submitted without an authorization header")
    public void a_logout_is_submitted_without_an_authorization_header() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/user/logout")).andReturn();
        captureResponse(mvcResult);
    }

    @When("{string} changes the password to {string}")
    public void changes_the_password_to(String username, String newPassword) throws Exception {
        changePassword(username, currentPassword(), newPassword);
    }

    @When("{string} changes the password to {string} using current password {string}")
    public void changes_the_password_to_using_current_password(String username,
                                                               String newPassword,
                                                               String oldPassword) throws Exception {
        changePassword(username, oldPassword, newPassword);
    }

    @When("a password change is submitted for {string}")
    public void a_password_change_is_submitted_for(String username) throws Exception {
        changePassword(username, currentPassword(), "new-password");
    }

    @Then("the response should contain a token")
    public void the_response_should_contain_a_token() {
        assertThat(tokenFromResponse())
                .as("expected a jwt-token in: %s", testContext.getResponseBody())
                .isNotNull()
                .isNotEmpty();
    }

    @Then("the response should not contain a token")
    public void the_response_should_not_contain_a_token() {
        assertThat(tokenFromResponse())
                .as("expected no jwt-token in: %s", testContext.getResponseBody())
                .isNull();
    }

    @Then("the token should be accepted on a protected endpoint")
    public void the_token_should_be_accepted_on_a_protected_endpoint() throws Exception {
        requestProtectedEndpoint(testContext.getToken());
        assertThat(testContext.getStatus()).isEqualTo(HttpStatus.OK);
    }

    @Then("the previous token should be rejected on a protected endpoint")
    public void the_previous_token_should_be_rejected_on_a_protected_endpoint() throws Exception {
        requestProtectedEndpoint(testContext.getToken());
        assertThat(testContext.getStatus().isError())
                .as("expected the token to be rejected but got %s", testContext.getStatus())
                .isTrue();
    }

    @Then("the login should be rejected")
    public void the_login_should_be_rejected() {
        assertThat(testContext.getStatus().isError())
                .as("expected the login to be rejected but got %s", testContext.getStatus())
                .isTrue();
    }

    @Then("the login should be rejected because the account is temporarily locked")
    public void the_login_should_be_rejected_because_the_account_is_temporarily_locked() {
        assertThat(testContext.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    }

    @Then("{string} should be able to log in with {string}")
    public void should_be_able_to_log_in_with(String username, String password) throws Exception {
        login(username, password);
        assertThat(testContext.getStatus()).isEqualTo(HttpStatus.OK);
    }

    @Then("the original password should no longer be accepted for {string}")
    public void the_original_password_should_no_longer_be_accepted_for(String username) throws Exception {
        login(username, currentPassword());
        assertThat(testContext.getStatus().isError())
                .as("expected the original password to be rejected but got %s", testContext.getStatus())
                .isTrue();
    }

    @Then("the original password should still be accepted for {string}")
    public void the_original_password_should_still_be_accepted_for(String username) throws Exception {
        login(username, currentPassword());
        assertThat(testContext.getStatus()).isEqualTo(HttpStatus.OK);
    }

    @Then("the stored password for {string} should not be {string}")
    public void the_stored_password_for_should_not_be(String username, String password) {
        User user = gymRepository.getUserRepository().findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("No user " + username));
        assertThat(user.getPassword()).isNotEqualTo(password);
    }

    private void failLogin(String username, Integer attempts) throws Exception {
        for (int attempt = 0; attempt < attempts; attempt++) {
            login(username, "wrong-password");
            assertThat(testContext.getStatus().isError())
                    .as("expected failed login %s of %s but got %s",
                            attempt + 1, attempts, testContext.getStatus())
                    .isTrue();
        }
    }

    private void login(String username, String password) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("username", username);
        body.put("password", password);

        MvcResult mvcResult = mockMvc.perform(get("/api/user/login")
                        .with(request -> {
                            request.setRemoteAddr(testContext.getClientAddress());
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();
        captureResponse(mvcResult);

        String token = tokenFromResponse();

        if (token != null) {
            testContext.setToken(token);
        }
    }

    private void changePassword(String username, String oldPassword, String newPassword) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("username", username);
        body.put("oldPassword", oldPassword);
        body.put("newPassword", newPassword);

        MvcResult mvcResult = mockMvc.perform(put("/api/user/password-change")
                        .header("Authorization", "Bearer " + testContext.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andReturn();
        captureResponse(mvcResult);
    }

    private void requestProtectedEndpoint(String token) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/training-types")
                        .header("Authorization", "Bearer " + token))
                .andReturn();
        captureResponse(mvcResult);
    }

    private void captureResponse(MvcResult mvcResult) throws Exception {
        testContext.setStatus(HttpStatus.valueOf(mvcResult.getResponse().getStatus()));
        testContext.setResponseBody(mvcResult.getResponse().getContentAsString());
    }

    private String currentPassword() {
        assertThat(testContext.getCurrentPassword())
                .as("no password has been established for this scenario yet")
                .isNotNull();
        return testContext.getCurrentPassword();
    }

    private String tokenFromResponse() {
        try {
            JsonNode root = objectMapper.readTree(testContext.getResponseBody());
            return root.hasNonNull("jwt-token") ? root.get("jwt-token").textValue() : null;
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}