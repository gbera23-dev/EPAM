package com.example.Trainer_history_service.restController;

import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.example.Trainer_history_service.entities.ActionType;
import com.example.Trainer_history_service.services.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Trainer Workload", description = "Endpoints for managing trainer workloads and training hours")
@RestController
@RequestMapping("/api/trainer")
public class TrainerRestController {

    private final TrainerService trainerService;

    public TrainerRestController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @Operation(
            summary = "Update trainer workload",
            description = "Creates the trainer's workload profile if it does not exist, " +
                    "then adds or removes training hours based on the action type in the request."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Workload updated successfully",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> updateTrainerWorkload(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Trainer workload request containing trainer details, date, duration, and action type",
                    required = true,
                    content = @Content(schema = @Schema(implementation = TrainerWorkloadRequest.class))
            )
            @RequestBody TrainerWorkloadRequest trainerWorkloadRequest) {

        createWorkloadIfNotExists(trainerWorkloadRequest);
        changeTrainingHours(trainerWorkloadRequest);

        return ResponseEntity.ok("trainer workload updated successfully!");
    }

    @Operation(
            summary = "Get trainer training hours",
            description = "Returns the total number of training hours logged for a specific trainer on a given date."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Training hours retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Integer.class))
            ),
            @ApiResponse(responseCode = "400", description = "Missing or invalid query parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer or training entry not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Integer> getTrainerHours(
            @Parameter(description = "Unique username of the trainer", required = true, example = "john.doe")
            @RequestParam("username") String username,

            @Parameter(description = "Date to query training hours for (ISO format: yyyy-MM-dd)", required = true, example = "2024-06-15")
            @RequestParam("date") LocalDate localDate) {

        int hours = trainerService.getTrainingHours(username, localDate);
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
        if (!trainerService.workloadExists(trainerWorkloadRequest.getUsername())) {
            createTrainerWorkload(trainerWorkloadRequest);
        }
    }

    private void changeTrainingHours(TrainerWorkloadRequest trainerWorkloadRequest) {
        if (trainerWorkloadRequest.getActionType().equals(ActionType.ADD)) {
            trainerService.addTrainingHours(
                    trainerWorkloadRequest.getUsername(),
                    trainerWorkloadRequest.getTrainingDate(),
                    trainerWorkloadRequest.getDuration()
            );
        } else {
            trainerService.deleteTrainingHours(
                    trainerWorkloadRequest.getUsername(),
                    trainerWorkloadRequest.getTrainingDate(),
                    trainerWorkloadRequest.getDuration()
            );
        }
    }
}