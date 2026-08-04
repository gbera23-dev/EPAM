package com.example.Trainer_history_service.cucumber;

import com.example.Trainer_history_service.documents.MonthlySummary;
import com.example.Trainer_history_service.documents.TrainerWorkload;
import com.example.Trainer_history_service.exceptions.MonthlySummaryFoundException;
import com.example.Trainer_history_service.repository.MonthlySummaryRepository;
import com.example.Trainer_history_service.repository.TrainerWorkloadRepository;
import io.cucumber.java.en.Given;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static com.example.Trainer_history_service.cucumber.TestUtils.splitUsername;
import static com.example.Trainer_history_service.cucumber.TestUtils.toLocalDate;

@RequiredArgsConstructor
public class TrainerWorkloadGivenSteps {

    private final MonthlySummaryRepository monthlySummaryRepository;
    private final TrainerWorkloadRepository trainerWorkloadRepository;

    @Given("a trainer workload exists with the following data")
    public void a_trainer_workload_exists_with_the_following_data(List<TrainerWorkloadDTO> trainerWorkloadDTOS) {
        trainerWorkloadDTOS.forEach(
                t -> {
                    TrainerWorkload trainerWorkload = new TrainerWorkload(
                            null, t.getUsername(), t.getFirstName(), t.getLastName(),
                            t.getIsActive(), null
                    );
                    trainerWorkloadRepository.save(trainerWorkload);
                }
        );
    }

    @Given("no monthly summary exists for {string} for month {string}")
    public void no_monthly_summary_exists_for_for_month(String username, String date) {

        TrainerWorkload trainerWorkload =
                trainerWorkloadRepository.findByUsername(username)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("username could not be found!"));

        Optional<MonthlySummary> optionalMonthlySummary = monthlySummaryRepository.
                findByTrainerWorkloadIdAndDate(trainerWorkload.getId(),
                        toLocalDate(date));

        if(optionalMonthlySummary.isPresent()) {
            throw new MonthlySummaryFoundException("Monthly summary was found!..");
        }

    }

    @Given("the trainer history service is running with an embedded Mongo")
    public void the_trainer_history_service_is_running_with_an_embedded_mongo() {
    }

    @Given("the trainer workload collection is empty")
    public void the_trainer_workload_collection_is_empty() {
        trainerWorkloadRepository.deleteAll(); //if it is not empty, we delete all data
    }

    @Given("the monthly summary collection is empty")
    public void the_monthly_summary_collection_is_empty() {
        monthlySummaryRepository.deleteAll(); //if it is not empty, we delete all data
    }

    @Given("a trainer workload exists for {string}")
    public void a_trainer_workload_exists_for(String username) {
        List<String> name = splitUsername(username);

        TrainerWorkload trainerWorkload = new TrainerWorkload(
                null, username, name.getFirst(), name.getLast(), true, null);
        trainerWorkloadRepository.save(trainerWorkload);
    }

    @Given("a monthly summary exists for {string} for month {string} with duration {int}")
    public void a_monthly_summary_exists_for_for_month_with_duration(String username,
                                                                     String date, Integer duration) {

        TrainerWorkload trainerWorkload =
                trainerWorkloadRepository.findByUsername(username)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("Username could not be found!")
                        );

        MonthlySummary monthlySummary = new MonthlySummary(
                null, toLocalDate(date), duration, trainerWorkload
        );
        monthlySummaryRepository.save(monthlySummary);
    }

    @Given("no trainer workload exists for {string}")
    public void no_trainer_workload_exists_for(String username) {
        List<MonthlySummary> monthlySummaries = monthlySummaryRepository.findAll();

        monthlySummaries.stream().filter(m ->
                m.getTrainerWorkload().getUsername().equals(username)).forEach(
                monthlySummaryRepository::delete
        );
        Optional<TrainerWorkload> optionalTrainerWorkload =
                trainerWorkloadRepository.findByUsername(username);

        optionalTrainerWorkload.ifPresent(trainerWorkloadRepository::delete);
    }

}
