package org.example.integration.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class TestSteps {

    private boolean frameworkRunning=false;

    @Given("the BDD test framework is running")
    public void the_bdd_test_framework_is_running() {
        frameworkRunning=true;
    }
    @When("it scans the features folder")
    public void it_scans_the_features_folder() {}

    @Then("it should discover and execute this scenario")
    public void it_should_discover_and_execute_this_scenario() {
        assertThat(frameworkRunning).isTrue();
    }
}
