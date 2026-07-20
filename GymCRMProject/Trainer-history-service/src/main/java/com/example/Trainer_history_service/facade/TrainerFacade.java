package com.example.Trainer_history_service.facade;

import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.services.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Component
public class TrainerFacade {

    private final TrainerService trainerService;

    public TrainerFacade(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    public ResponseEntity<String> updateTrainerWorkload(TrainerWorkloadRequest trainerWorkloadRequest) {

        trainerService.updateTrainingHours(trainerWorkloadRequest);

        return ResponseEntity.ok("trainer workload updated successfully!");
    }

    public ResponseEntity<String> updateTrainersWorkloadInBatch(List<TrainerWorkloadRequest> trainerWorkloadRequests) {

        trainerService.updateTrainingHoursInBatch(trainerWorkloadRequests);

        return ResponseEntity.ok("Workloads have been updated successfully!");
    }

    public ResponseEntity<Integer> getTrainerHours(String username, LocalDate localDate) {

        int hours = trainerService.getTrainingHours(username, localDate);

        return ResponseEntity.ok(hours);
    }

}