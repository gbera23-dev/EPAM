package com.example.Trainer_history_service.integration.cucumber;

import com.example.Trainer_history_service.domain.documents.MonthlySummary;
import com.example.Trainer_history_service.domain.documents.TrainerWorkload;
import com.example.Trainer_history_service.domain.exceptions.MonthlySummaryNotFoundException;
import com.example.Trainer_history_service.domain.repository.MonthlySummaryRepository;
import com.example.Trainer_history_service.domain.repository.TrainerWorkloadRepository;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static com.example.Trainer_history_service.integration.cucumber.TestUtils.toLocalDate;
import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class TrainerWorkloadThenSteps {
    private final MonthlySummaryRepository monthlySummaryRepository;
    private final TrainerWorkloadRepository trainerWorkloadRepository;
    private final TestContext testContext;

    @Then("a trainer workload should exist for {string}")
    public void a_trainer_workload_should_exist_for(String username) {
        assertThat(trainerWorkloadRepository.existsByUsername(username)).isTrue();
    }

    @Then("the workload for {string} should be active")
    public void the_workload_for_should_be_active(String username) {
        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Username could not be found!")
                );
        assertThat(trainerWorkload.isActive()).isTrue();
    }

    @Then("the workload for {string} should be inactive")
    public void the_workload_for_should_be_inactive(String username) {
        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Username could not be found!")
                );
        assertThat(!trainerWorkload.isActive()).isTrue();
    }

    @Then("the monthly summary for {string} for month {string} should have duration {int}")
    public void the_monthly_summary_for_for_month_should_have_duration(String username,
                                                                       String date,
                                                                       Integer expectedDuration) {
        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Username could not be found!")
                );
        MonthlySummary monthlySummary =
                monthlySummaryRepository.findByTrainerWorkloadIdAndDate(trainerWorkload.getId(),
                                toLocalDate(date))
                        .orElseThrow(() ->
                                new MonthlySummaryNotFoundException("Could not find monthly summary!"));
        assertThat(monthlySummary.getDuration()).isEqualTo(expectedDuration);
    }

    @Then("no monthly summary should exist for {string} for month {string}")
    public void no_monthly_summary_should_exist_for_for_month(String username, String date) {
        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Username could not be found!")
                );
        Optional<MonthlySummary> optionalMonthlySummary =
                monthlySummaryRepository.findByTrainerWorkloadIdAndDate(trainerWorkload.getId(),
                        toLocalDate(date));
        assertThat(optionalMonthlySummary.isEmpty()).isTrue();
    }

    @Then("the update should be rejected")
    public void the_update_should_be_rejected() {
        assertThat(testContext.getLastException()).isNotNull();
    }

    @Then("the update should be rejected with a message mentioning {string}")
    public void the_update_should_be_rejected_with_a_message_mentioning(String message) {
        assertThat(testContext.getLastException().getMessage()).isEqualTo(message);
    }

    @Then("the workload for {string} should have first name {string} and last name {string}")
    public void the_workload_for_should_have_first_name_and_last_name
            (String username, String firstName, String lastName) {
        TrainerWorkload trainerWorkload = trainerWorkloadRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("Username could not be found!")
                );
        assertThat(trainerWorkload.getFirstName().equals(firstName) &&
                trainerWorkload.getLastName().equals(lastName)).isTrue();
    }

    @Then("exactly {int} monthly summary should exist for {string}")
    public void exactly_monthly_summary_should_exist_for(Integer monthlySummaryCount,
                                                         String username) {

        List<MonthlySummary> monthlySummaries = monthlySummaryRepository.findAll();
        int total = (int)monthlySummaries.stream().filter(m ->
                        m.getTrainerWorkload().getUsername().equals(username))
                .count();

        assertThat(monthlySummaryCount).isEqualTo(total);
    }
}
