package org.example.integration.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.bson.Document;

import java.time.Duration;
import java.time.YearMonth;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class WorkloadSteps {

    private static final Duration STEADY_WINDOW = Duration.ofSeconds(3);

    private final TestContext testContext;
    private final GymCRMApi GymCRMApi;
    private final TrainerHistoryServiceConnection trainerHistoryServiceConnection;

    public WorkloadSteps(TestContext testContext, GymCRMApi GymCRMApi, TrainerHistoryServiceConnection trainerHistoryServiceConnection) {
        this.testContext = testContext;
        this.GymCRMApi = GymCRMApi;
        this.trainerHistoryServiceConnection = trainerHistoryServiceConnection;
    }

    @Given("a registered trainee")
    public void a_registered_trainee() throws Exception {
        GymCRMApi.Credentials credentials = GymCRMApi.registerTrainee();
        testContext.setTraineeUsername(credentials.username());
        testContext.setToken(GymCRMApi.login(credentials.username(), credentials.password()));
    }

    @Given("another registered trainee")
    public void another_registered_trainee() throws Exception {
        testContext.setSecondTraineeUsername(GymCRMApi.registerTrainee().username());
    }

    @Given("a registered trainer")
    public void a_registered_trainer() throws Exception {
        testContext.setTrainerUsername(GymCRMApi.registerTrainer(testContext.getToken()).username());
    }

    @Given("another registered trainer")
    public void another_registered_trainer() throws Exception {
        testContext.setSecondTrainerUsername(GymCRMApi.registerTrainer(testContext.getToken()).username());
    }

    @Given("the trainee is logged in")
    public void the_trainee_is_logged_in() {
        assertThat(testContext.getToken())
                .as("the trainee should have been logged in during registration")
                .isNotNull();
    }

    @When("a training is added for the trainee with the trainer on {string} lasting {int} hours")
    @Given("a training was added for the trainee with the trainer on {string} lasting {int} hours")
    public void a_training_is_added_with_the_trainer(String date, Integer duration) throws Exception {
        addTraining(testContext.getTraineeUsername(), testContext.getTrainerUsername(), date, duration);
    }

    @When("a training is added for the trainee with the second trainer on {string} lasting {int} hours")
    @Given("a training was added for the trainee with the second trainer on {string} lasting {int} hours")
    public void a_training_is_added_with_the_second_trainer(String date, Integer duration) throws Exception {
        addTraining(testContext.getTraineeUsername(), testContext.getSecondTrainerUsername(), date, duration);
    }

    @When("a training is added for the second trainee with the trainer on {string} lasting {int} hours")
    @Given("a training was added for the second trainee with the trainer on {string} lasting {int} hours")
    public void a_training_is_added_for_the_second_trainee(String date, Integer duration) throws Exception {
        addTraining(testContext.getSecondTraineeUsername(), testContext.getTrainerUsername(), date, duration);
    }

    @When("a training is added for an unknown trainee with the trainer on {string} lasting {int} hours")
    public void a_training_is_added_for_an_unknown_trainee(String date, Integer duration) throws Exception {
        addTraining("no.such.trainee", testContext.getTrainerUsername(), date, duration);
    }

    @When("a training is added for the trainee with an unknown trainer on {string} lasting {int} hours")
    public void a_training_is_added_with_an_unknown_trainer(String date, Integer duration) throws Exception {
        addTraining(testContext.getTraineeUsername(), "no.such.trainer", date, duration);
    }

    @When("that training is deleted")
    public void that_training_is_deleted() throws Exception {
        deleteTrainingNamed(testContext.getLastTrainingName());
    }

    @When("the training on {string} is deleted")
    public void the_training_on_is_deleted(String date) throws Exception {
        deleteTrainingNamed(testContext.trainingNameOn(date));
    }

    @When("a training that does not exist is deleted")
    public void a_training_that_does_not_exist_is_deleted() throws Exception {
        testContext.setLastStatus(GymCRMApi.deleteTraining(testContext.getToken(), 999_999_999L).status());
    }

    @When("the trainee is deleted")
    public void the_trainee_is_deleted() throws Exception {
        testContext.setLastStatus(GymCRMApi.deleteTrainee(testContext.getToken(), testContext.getTraineeUsername()).status());
    }

    @When("an unknown trainee is deleted")
    public void an_unknown_trainee_is_deleted() throws Exception {
        testContext.setLastStatus(GymCRMApi.deleteTrainee(testContext.getToken(), "no.such.trainee").status());
    }

    @Then("the request should be accepted")
    public void the_request_should_be_accepted() {
        assertThat(testContext.getLastStatus()).isEqualTo(200);
    }

    @Then("the request should be rejected as not found")
    public void the_request_should_be_rejected_as_not_found() {
        assertThat(testContext.getLastStatus()).isEqualTo(404);
    }

    @Then("^the history service should eventually record (\\d+) hours for the (trainer|second trainer) in \"([^\"]*)\"$")
    @Given("^the history service eventually records (\\d+) hours for the (trainer|second trainer) in \"([^\"]*)\"$")
    public void the_history_service_should_eventually_record_hours(int expected, String who, String month) {
        String trainer = trainerFor(who);
        YearMonth yearMonth = YearMonth.parse(month);

        await().atMost(Config.MESSAGE_TIMEOUT)
                .pollInterval(Config.POLL_INTERVAL)
                .untilAsserted(() -> assertThat(trainerHistoryServiceConnection.hoursFor(trainer, yearMonth))
                        .as("hours recorded for %s in %s", trainer, month)
                        .isEqualTo(expected));
    }

    @Then("^the history service should eventually record no hours for the (trainer|second trainer) in \"([^\"]*)\"$")
    public void the_history_service_should_eventually_record_no_hours(String who, String month) {
        the_history_service_should_eventually_record_hours(0, who, month);
    }

    @Then("^the history service should record no hours for the (trainer|second trainer) in \"([^\"]*)\"$")
    public void the_history_service_should_record_no_hours(String who, String month) {
        assertThat(trainerHistoryServiceConnection.hoursFor(trainerFor(who), YearMonth.parse(month)))
                .as("hours recorded for %s in %s", trainerFor(who), month)
                .isZero();
    }

    @Then("^the history service should record no hours for the (trainer|second trainer) in \"([^\"]*)\" for the next (\\d+) seconds$")
    public void the_history_service_should_record_no_hours_for(String who, String month, int seconds) {
        hoursShouldStayAt(0, who, month, seconds);
    }

    @Then("^the history service should keep recording (\\d+) hours for the (trainer|second trainer) in \"([^\"]*)\" for the next (\\d+) seconds$")
    public void the_history_service_should_keep_recording(int expected, String who, String month, int seconds) {
        hoursShouldStayAt(expected, who, month, seconds);
    }

    @Then("the history service should know the trainer's first name, last name and active flag")
    public void the_history_service_should_know_the_trainer_profile() {
        String trainer = testContext.getTrainerUsername();

        await().atMost(Config.MESSAGE_TIMEOUT)
                .pollInterval(Config.POLL_INTERVAL)
                .untilAsserted(() -> assertThat(trainerHistoryServiceConnection.workloadOf(trainer))
                        .as("workload document for %s", trainer)
                        .isNotNull());

        Document workload = trainerHistoryServiceConnection.workloadOf(trainer);
        assertThat(workload.getString("first_name")).isNotBlank();
        assertThat(workload.getString("last_name")).isNotBlank();
        assertThat(workload.getBoolean("status")).isTrue();
    }

    private void hoursShouldStayAt(int expected, String who, String month, int seconds) {
        String trainer = trainerFor(who);
        YearMonth yearMonth = YearMonth.parse(month);
        Duration window = seconds > 0 ? Duration.ofSeconds(seconds) : STEADY_WINDOW;

        await().during(window)
                .atMost(window.plusSeconds(2))
                .pollInterval(Config.POLL_INTERVAL)
                .untilAsserted(() -> assertThat(trainerHistoryServiceConnection.hoursFor(trainer, yearMonth))
                        .as("hours recorded for %s in %s", trainer, month)
                        .isEqualTo(expected));
    }

    private void addTraining(String traineeUsername, String trainerUsername, String date, int duration)
            throws Exception {
        String trainingName = "E2E-" + ThreadLocalRandom.current().nextInt(100_000, 999_999);

        GymCRMApi.Response response = GymCRMApi.addTraining(
                testContext.getToken(), traineeUsername, trainerUsername, trainingName, date, duration);

        testContext.setLastStatus(response.status());
        if (response.status() == 200) {
            testContext.rememberTraining(date, trainingName);
        }
    }

    private void deleteTrainingNamed(String trainingName) throws Exception {
        long trainingId = GymCRMApi.trainingIdOf(testContext.getToken(), testContext.getTraineeUsername(), trainingName);
        testContext.setLastStatus(GymCRMApi.deleteTraining(testContext.getToken(), trainingId).status());
    }

    private String trainerFor(String who) {
        return "second trainer".equals(who) ? testContext.getSecondTrainerUsername() : testContext.getTrainerUsername();
    }
}