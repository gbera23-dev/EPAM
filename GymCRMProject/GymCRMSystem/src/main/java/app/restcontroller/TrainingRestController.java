package app.restcontroller;

import app.annotations.InteractsWithTraineeHistoryService;
import app.dto.api.request.TrainingRequest;
import app.strategies.MicroserviceInteraction.AddHoursToTrainerStrategy;
import app.strategies.MicroserviceInteraction.RemoveHoursFromTrainerStrategy;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.services.TrainingService;

@RestController
@RequestMapping("/api/trainings")
@Tag(name = "Training Management", description = "Operations for adding new trainings")
public class TrainingRestController {

    private final TrainingService trainingService;

    public TrainingRestController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }


    @Operation(summary = "Add a new training", description = "Creates a training session between a trainee and a trainer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training added successfully",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found", content = @Content)
    })
    @InteractsWithTraineeHistoryService(chosenStrategy = AddHoursToTrainerStrategy.class)
    @PostMapping
    public ResponseEntity<String> addTraining(@Valid @RequestBody TrainingRequest trainingRequest,
                                              HttpServletRequest httpServletRequest) {

        trainingService.addTraining(
                trainingRequest.getTraineeUsername(),
                trainingRequest.getTrainerUsername(),
                trainingRequest.getTrainingName(),
                trainingRequest.getDate(),
                trainingRequest.getDuration()
        );

        return ResponseEntity.ok("Training was added successfully!");
    }

    @Operation(summary = "Delete existing training", description = "Deletes a particular training based on its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training deleted successfully",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Training not found", content = @Content)
    })
    @InteractsWithTraineeHistoryService(chosenStrategy = RemoveHoursFromTrainerStrategy.class)
    @DeleteMapping("/{training-id}")
    public ResponseEntity<String> deleteTraining(@Valid @PathVariable("training-id") Long trainingId,
                                                 HttpServletRequest httpServletRequest) {

        trainingService.deleteTraining(trainingId);

        return ResponseEntity.status(HttpStatus.OK).body("Training was deleted successfully!");
    }

}
