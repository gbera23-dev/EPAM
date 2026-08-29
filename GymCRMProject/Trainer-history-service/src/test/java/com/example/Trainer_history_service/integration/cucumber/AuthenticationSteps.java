package com.example.Trainer_history_service.integration.cucumber;

import com.example.Trainer_history_service.api.exceptions.UserCannotBeAuthorizedException;
import com.example.Trainer_history_service.services.security.interfaces.JWTService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class AuthenticationSteps {

    private final JWTService jwtService;
    private final JwtTokenFactory tokens;
    private final TestContext context;

    @Given("the service is configured with a JWT signing key")
    public void the_service_is_configured_with_a_jwt_signing_key() {
        assertThat(!tokens.configuredKey().isBlank());
    }

    @Given("a JWT token signed with the configured key for subject {string}")
    public void a_jwt_token_signed_with_the_configured_key_for_subject(String subject) {
        context.setToken(tokens.validToken(subject, Duration.ofMinutes(30)));
    }
    @When("the username is extracted from the token")
    public void the_username_is_extracted_from_the_token() {
        String extractedToken = null;
        try {
            extractedToken = jwtService.extractUsernameFromToken(context.getToken());
        }
        catch(JwtException | UserCannotBeAuthorizedException e) {
            context.setLastException(e);
        }
        finally {
            context.setExtractedUsername(extractedToken);
        }
    }
    @Then("the extracted username should be {string}")
    public void the_extracted_username_should_be(String username) {
        if(context.getExtractedUsername() == null) {
            throw new UsernameNotFoundException("could not extract username!");
        }
        assertThat(context.getExtractedUsername().equals(username));
    }

    @Given("a JWT token signed with the configured key with no subject claim")
    public void a_jwt_token_signed_with_the_configured_key_with_no_subject_claim() {
        context.setToken(tokens.tokenWithoutSubject());
    }

    @Given("a JWT token signed with the configured key for subject {string} that expires in {int} minutes")
    public void a_jwt_token_signed_with_the_configured_key_for_subject_that_expires_in_minutes(String subject, Integer minutes) {
        context.setToken(tokens.validToken(subject, Duration.ofMinutes(minutes)));
    }
    @When("the token is checked for validity")
    public void the_token_is_checked_for_validity() {
        try {
            context.setTokenValid(jwtService.tokenIsValid(context.getToken()));
            context.setLastException(null);
        } catch (RuntimeException exception) {
            context.setLastException(exception);
            context.setTokenValid(null);
        }
    }
    @Then("the token should be reported as valid")
    public void the_token_should_be_reported_as_valid() {
        assertThat(context.getTokenValid() != null && context.getTokenValid());
    }

    @Given("a {string} JWT token")
    public void a_jwt_token(String kind) {
        context.setToken(tokens.tokenOfKind(kind));
    }

    @Then("the operation should fail with a {string}")
    public void the_operation_should_fail_with_a(String exceptionName) {
        assertThat(context.getLastException())
                .as("expected a %s to be thrown", exceptionName)
                .isNotNull();
        assertThat(context.getLastException().getClass().getSimpleName()).isEqualTo(exceptionName);    }
}
