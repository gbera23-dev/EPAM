package app.restcontroller;

import app.dto.api.request.TrainerRegistrationRequest;
import app.dto.api.request.TrainerTrainingsRequest;
import app.dto.api.request.TrainerUpdateRequest;
import app.dto.api.response.TrainerProfileResponse;
import app.dto.api.response.TrainingResponse;
import app.dto.api.response.UserCredentialsResponse;
import app.entities.Trainer;
import app.entities.Training;
import app.entities.TrainingType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import app.mappers.api.TrainerApiMapper;
import app.mappers.api.TrainingApiMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.services.TrainerService;
import app.services.TrainingTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/trainer")
@Tag(name = "Trainer Management", description = "Operations for trainers")
public class TrainerRestController {

    private final TrainerService trainerService;
    private final TrainerApiMapper trainerApiMapper;
    private final TrainingApiMapper trainingApiMapper;
    private final TrainingTypeService trainingTypeService;

    public TrainerRestController(TrainerService trainerService, TrainerApiMapper trainerApiMapper, TrainingApiMapper trainingApiMapper,
                                 TrainingTypeService trainingTypeService) {
        this.trainerService = trainerService;
        this.trainerApiMapper = trainerApiMapper;
        this.trainingApiMapper = trainingApiMapper;
        this.trainingTypeService = trainingTypeService;
    }



    @Operation(summary = "Register a new trainer", description = "Creates a new trainer profile and returns generated credentials")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainer registered successfully",
                    content = @Content(schema = @Schema(implementation = UserCredentialsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "404", description = "Training type not found", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<UserCredentialsResponse> registerTrainer
            (@RequestBody TrainerRegistrationRequest trainerRegistrationRequest) {

        TrainingType trainingType = this.trainingTypeService.getTrainingTypeById(trainerRegistrationRequest.
                getSpecialization().getTrainingTypeId());

        Trainer trainer = trainerApiMapper.toTrainer(trainerRegistrationRequest, trainingType);

        String rawPassword = trainerService.createTrainerProfile(trainer);

        return ResponseEntity.status(HttpStatus.CREATED).
                body(new UserCredentialsResponse(trainer.getUser().getUsername(), rawPassword));
    }



    @Operation(summary = "Get trainer profile", description = "Returns full profile of a trainer by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer found",
                    content = @Content(schema = @Schema(implementation = TrainerProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Username is blank", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    
    @GetMapping
    public ResponseEntity<TrainerProfileResponse> getTrainer
            (@NotBlank @RequestParam("username") String username) {

        Trainer trainer = trainerService.
                selectTrainerProfileByUsername(username);

        return ResponseEntity.ok().body(
                trainerApiMapper.toTrainerProfileResponse(trainer));
    }


    @Operation(summary = "Update trainer profile", description = "Updates an existing trainer's information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer updated successfully",
                    content = @Content(schema = @Schema(implementation = TrainerProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    
    @PutMapping("/update")
    public ResponseEntity<TrainerProfileResponse> updateTrainer
            (@Valid @RequestBody TrainerUpdateRequest trainerUpdateRequest) {

        Trainer trainer = trainerService.selectTrainerProfileByUsername(trainerUpdateRequest.getUsername());

        updateTrainerData(trainer, trainerUpdateRequest);

        trainerService.updateTrainerProfile(trainer);

        return ResponseEntity.ok().body(
                trainerApiMapper.toTrainerProfileResponse(trainer));
    }


    @Operation(summary = "Get trainer's trainings", description = "Returns filtered list of trainings for a trainer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainings retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainingResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    
    @GetMapping("/trainings")
    public ResponseEntity<List<TrainingResponse>> getTrainerTrainingsList
            (@Valid @RequestBody TrainerTrainingsRequest trainerTrainingsRequest) {

        List<Training> trainings = trainerService.getTrainingsForTrainer(
                trainerTrainingsRequest.getUsername(),
                trainerTrainingsRequest.getFrom(),
                trainerTrainingsRequest.getTo(),
                trainerTrainingsRequest.getTraineeName()
        );

        return ResponseEntity.ok().body(
                trainings.stream().map(
                                trainingApiMapper::toTrainingResponse
                ).toList()
        );
    }


    @Operation(summary = "Activate or deactivate trainer", description = "Toggles the active status of a trainer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status changed successfully",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid path variable or param", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainer not found", content = @Content)
    })
    
    @PatchMapping("/{username}/status")
    public ResponseEntity<String> changeTrainerActiveStatus
            (@NotBlank @PathVariable("username") String username,
             @NotNull @RequestParam("active") Boolean isActive) {

        Trainer trainer = trainerService.
                selectTrainerProfileByUsername(username);

        if(isActive) {
            trainerService.activateTrainerProfile(trainer.getId());
            return ResponseEntity.ok("trainer has been successfully activated!");
        }

        trainerService.deactivateTrainerProfile(trainer.getId());
        return ResponseEntity.ok("trainer has been successfully deactivated!");
    }

    /**
     * Takes update request data and sets appropriate fields of the trainer object
     * @param trainer object to be updated
     * @param trainerUpdateRequest data that is used to update trainer
     */
    private void updateTrainerData(Trainer trainer, TrainerUpdateRequest trainerUpdateRequest) {
        trainer.getUser().setFirstName(trainerUpdateRequest.getFirstName());
        trainer.getUser().setLastName(trainerUpdateRequest.getLastName());
        trainer.getUser().setActive(trainerUpdateRequest.getIsActive());

        if(trainerUpdateRequest.getSpecialization() != null) {
            TrainingType trainingType = trainingTypeService.getTrainingTypeById
                    (trainerUpdateRequest.getSpecialization().getTrainingTypeId());
            trainer.setTrainingType(trainingType);
        }
    }


}
