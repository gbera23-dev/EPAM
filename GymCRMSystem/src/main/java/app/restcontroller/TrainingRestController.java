package app.restcontroller;

import app.dto.api.request.TrainingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    
    @PostMapping
    public ResponseEntity<String> addTraining(@Valid @RequestBody TrainingRequest trainingRequest) {

        trainingService.addTraining(
                trainingRequest.getTraineeUsername(),
                trainingRequest.getTrainerUsername(),
                trainingRequest.getTrainingName(),
                trainingRequest.getDate(),
                trainingRequest.getDuration()
        );

        return ResponseEntity.ok("Training was added successfully!");
    }

}
