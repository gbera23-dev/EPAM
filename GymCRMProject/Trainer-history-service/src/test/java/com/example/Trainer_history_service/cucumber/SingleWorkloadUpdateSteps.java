package com.example.Trainer_history_service.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.*;
import io.cucumber.java.*; 

public class SingleWorkloadUpdateSteps {

    @When("the following workload message is sent to {string}")
    public void the_following_workload_message_is_sent_to(String string, DataTable dataTable) {
        // Write code here that turns the phrase above into concrete actions
        // For automatic transformation, change DataTable to one of
        // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
        // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
        // Double, Byte, Short, Long, BigInteger or BigDecimal.
        //
        // For other transformations you can register a DataTableType.
        throw new PendingException();
    }

    @Then("the workload for {string} should be active")
    public void the_workload_for_should_be_active(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("a trainer workload exists with the following data")
    public void a_trainer_workload_exists_with_the_following_data(DataTable dataTable) {
        // Write code here that turns the phrase above into concrete actions
        // For automatic transformation, change DataTable to one of
        // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
        // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
        // Double, Byte, Short, Long, BigInteger or BigDecimal.
        //
        // For other transformations you can register a DataTableType.
        throw new PendingException();
    }

    @Then("exactly {int} trainer workload should exist for {string}")
    public void exactly_trainer_workload_should_exist_for(Integer int1, String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("the workload for {string} should be inactive")
    public void the_workload_for_should_be_inactive(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("an {string} workload message is sent for {string} on {string} with duration {int}")
    public void an_workload_message_is_sent_for_on_with_duration(String string, String string2, String string3, Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("a {string} workload message is sent for {string} on {string} with duration {int}")
    public void a_workload_message_is_sent_for_on_with_duration(String string, String string2, String string3, Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("a trainer workload should exist for {string}")
    public void a_trainer_workload_should_exist_for(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("the monthly summary for {string} for month {string} should have duration {int}")
    public void the_monthly_summary_for_for_month_should_have_duration(String string, String string2, Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("no monthly summary exists for {string} for month {string}")
    public void no_monthly_summary_exists_for_for_month(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("a message processing failure should be logged")
    public void a_message_processing_failure_should_be_logged() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("no monthly summary should exist for {string} for month {string}")
    public void no_monthly_summary_should_exist_for_for_month(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
}
