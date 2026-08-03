package com.example.Trainer_history_service.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.*;
import io.cucumber.java.*;

public class BatchWorkloadUpdateSteps {

    @Given("the trainer history service is running with an embedded Mongo and an embedded JMS broker")
    public void the_trainer_history_service_is_running_with_an_embedded_mongo_and_an_embedded_jms_broker() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    @Given("the trainer workload collection is empty")
    public void the_trainer_workload_collection_is_empty() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    @Given("the monthly summary collection is empty")
    public void the_monthly_summary_collection_is_empty() {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    @Given("a trainer workload exists for {string}")
    public void a_trainer_workload_exists_for(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    @When("the following batch workload message is sent to {string}")
    public void the_following_batch_workload_message_is_sent_to(String string, DataTable dataTable) {
        // Write code here that turns the phrase above into concrete actions
        // For automatic transformation, change DataTable to one of
        // E, List<E>, List<List<E>>, List<Map<K,V>>, Map<K,V> or
        // Map<K, List<V>>. E,K,V must be a String, Integer, Float,
        // Double, Byte, Short, Long, BigInteger or BigDecimal.
        //
        // For other transformations you can register a DataTableType.
        throw new PendingException();
    }
    @Then("the monthly summary for {string} for month {string} should eventually have duration {int}")
    public void the_monthly_summary_for_for_month_should_eventually_have_duration(String string, String string2, Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    @Then("exactly {int} monthly summary should exist for {string}")
    public void exactly_monthly_summary_should_exist_for(Integer int1, String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }


    @Given("a monthly summary exists for {string} for month {string} with duration {int}")
    public void a_monthly_summary_exists_for_for_month_with_duration(String string, String string2, Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("no monthly summary should eventually exist for {string} for month {string}")
    public void no_monthly_summary_should_eventually_exist_for_for_month(String string, String string2) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("a message processing failure should be logged mentioning {string}")
    public void a_message_processing_failure_should_be_logged_mentioning(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    @Then("the monthly summary for {string} for month {string} should still have duration {int}")
    public void the_monthly_summary_for_for_month_should_still_have_duration(String string, String string2, Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("exactly {int} monthly summaries should exist for {string}")
    public void exactly_monthly_summaries_should_exist_for(Integer int1, String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Given("no trainer workload exists for {string}")
    public void no_trainer_workload_exists_for(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @Then("a trainer workload should eventually exist for {string}")
    public void a_trainer_workload_should_eventually_exist_for(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }
    @Then("the workload for {string} should have first name {string} and last name {string}")
    public void the_workload_for_should_have_first_name_and_last_name(String string, String string2, String string3) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

    @When("an empty batch workload message is sent to {string}")
    public void an_empty_batch_workload_message_is_sent_to(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new PendingException();
    }

}
