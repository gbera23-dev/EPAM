package com.example.Trainer_history_service.restController;

import com.example.Trainer_history_service.dto.TrainerHoursRequest;
import com.example.Trainer_history_service.dto.TrainerWorkloadCreationRequest;
import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.entities.ActionType;
import com.example.Trainer_history_service.services.TrainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainer")
public class TrainerRestController {

    private final TrainerService trainerService;

    public TrainerRestController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @PostMapping
    public ResponseEntity<String> updateTrainerWorkload(
            @RequestBody TrainerWorkloadRequest trainerWorkloadRequest) {

            createWorkloadIfNotExists(trainerWorkloadRequest);

            changeTrainingHours(trainerWorkloadRequest);

        return ResponseEntity.ok(
                "trainer workload updated successfully!");
    }


    @GetMapping
    public ResponseEntity<Integer> getTrainerHours(
            @RequestBody TrainerHoursRequest trainerHoursRequest) {

        int hours = trainerService.getTrainingHours(trainerHoursRequest.getUsername(),
                trainerHoursRequest.getDate());

        return ResponseEntity.ok(hours);
    }


    private void createTrainerWorkload(TrainerWorkloadRequest trainerWorkloadRequest) {
        trainerService.createNewWorkload(
                trainerWorkloadRequest.getUsername(),
                trainerWorkloadRequest.getFirstName(),
                trainerWorkloadRequest.getLastName(),
                trainerWorkloadRequest.getIsActive()
        );
    }

    private void createWorkloadIfNotExists(TrainerWorkloadRequest trainerWorkloadRequest) {
        if(!trainerService.workloadExists(trainerWorkloadRequest.getUsername())) {
            createTrainerWorkload(trainerWorkloadRequest);
        }
    }

    private void changeTrainingHours(TrainerWorkloadRequest trainerWorkloadRequest) {
        if(trainerWorkloadRequest.getActionType().equals(ActionType.ADD)){
            trainerService.addTrainingHours(trainerWorkloadRequest.getUsername(),
                    trainerWorkloadRequest.getTrainingDate(),
                    trainerWorkloadRequest.getDuration()
            );
        }
        else {
            trainerService.deleteTrainingHours(trainerWorkloadRequest.getUsername(),
                    trainerWorkloadRequest.getTrainingDate(),
                    trainerWorkloadRequest.getDuration());
        }
    }

}
