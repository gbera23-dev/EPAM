package com.example.Trainer_history_service.integration.cucumber;

import com.example.Trainer_history_service.documents.ActionType;
import com.example.Trainer_history_service.documents.TrainerWorkload;
import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.facade.TrainerFacade;
import com.example.Trainer_history_service.repository.TrainerWorkloadRepository;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static com.example.Trainer_history_service.integration.cucumber.TestUtils.toLocalDate;

@RequiredArgsConstructor
public class TrainerWorkloadWhenSteps {


    private final TrainerWorkloadRepository trainerWorkloadRepository;
    private final TrainerFacade trainerFacade;
    private final TestContext testContext;

    @When("the following workload update is submitted to the facade")
    public void the_following_workload_update_is_submitted_to_the_facade(
            List<TrainerWorkloadRequest> trainerWorkloadRequestList) {

        try {
            trainerWorkloadRequestList.forEach(
                    trainerFacade::updateTrainerWorkload
            );
        } catch (RuntimeException e) {
            testContext.setLastException(e);
        }
    }

    @When("a(n) {string} workload update is submitted for {string} on {string} with duration {int}")
    public void an_workload_update_is_submitted_for_on_with_duration(String actionType,
                                                                     String username,
                                                                     String trainingDate,
                                                                     Integer duration) {
        Optional<TrainerWorkload> optionalTrainerWorkload =
                trainerWorkloadRepository.findByUsername(username);

        if(optionalTrainerWorkload.isEmpty()) {
            testContext.setLastException(
                    new UsernameNotFoundException("Username could not be found!"));
            return;
        }
        TrainerWorkload trainerWorkload = optionalTrainerWorkload.get();

        TrainerWorkloadRequest trainerWorkloadRequest = new TrainerWorkloadRequest(
                username,
                trainerWorkload.getFirstName(),
                trainerWorkload.getLastName(),
                trainerWorkload.isActive(),
                toLocalDate(trainingDate),
                duration,
                ActionType.valueOf(actionType)
        );
        try {
            trainerFacade.updateTrainerWorkload(trainerWorkloadRequest);
        }
        catch (RuntimeException e) {
            testContext.setLastException(e);
        }
    }

    @When("the following batch workload update is submitted to the facade")
    public void the_following_batch_workload_update_is_submitted_to_the_facade
            (List<TrainerWorkloadRequest> trainerWorkloadRequestList) {

        try {
            trainerFacade.updateTrainersWorkloadInBatch(trainerWorkloadRequestList);
        } catch (RuntimeException e) {
            testContext.setLastException(e);
        }
    }

    @When("an empty batch workload update is submitted to the facade")
    public void an_empty_batch_workload_update_is_submitted_to_the_facade() {
        try {
            trainerFacade.updateTrainersWorkloadInBatch(List.of());
        } catch (RuntimeException e) {
            testContext.setLastException(e);
        }
    }

}
