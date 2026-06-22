package app.restcontroller;

import app.dto.api.request.*;
import app.dto.api.response.*;
import app.entities.*;
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
import app.mappers.api.TraineeApiMapper;
import app.mappers.api.TrainerApiMapper;
import app.mappers.api.TrainingApiMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.services.TraineeService;

import java.util.List;

@RestController
@RequestMapping("/api/trainee")
@Tag(name = "Trainee Management", description = "Operations for trainees")
public class TraineeRestController {

    private final TraineeService traineeService;
    private final TraineeApiMapper traineeApiMapper;
    private final TrainerApiMapper trainerApiMapper;
    private final TrainingApiMapper trainingApiMapper;

    public TraineeRestController(TraineeService traineeService,
                                 TraineeApiMapper traineeApiMapper, TrainerApiMapper trainerApiMapper,
                                 TrainingApiMapper trainingApiMapper) {
        this.traineeService = traineeService;
        this.traineeApiMapper = traineeApiMapper;
        this.trainerApiMapper = trainerApiMapper;
        this.trainingApiMapper = trainingApiMapper;
    }


    @Operation(summary = "Register a new trainee", description = "Creates a new trainee profile and returns generated credentials")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Trainee registered successfully",
                    content = @Content(schema = @Schema(implementation = UserCredentialsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<UserCredentialsResponse> registerTrainee
            (@Valid @RequestBody TraineeRegistrationRequest traineeRegistrationRequest) {
        Trainee trainee = traineeApiMapper.toTrainee(traineeRegistrationRequest);

        String rawPassword = traineeService.createTraineeProfile(trainee);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserCredentialsResponse(trainee.getUser().getUsername(), rawPassword));
    }


    @Operation(summary = "Get trainee profile", description = "Returns full profile of a trainee by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee found",
                    content = @Content(schema = @Schema(implementation = TraineeProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Username is blank", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    
    @GetMapping
    public ResponseEntity<TraineeProfileResponse> getTrainee(@NotBlank @RequestParam("username") String username) {

        Trainee trainee = traineeService.selectTraineeProfileByUsername(username);

        return ResponseEntity.ok().body(traineeApiMapper.toTraineeProfileResponse(trainee));
    }


    @Operation(summary = "Update trainee profile", description = "Updates an existing trainee's information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee updated successfully",
                    content = @Content(schema = @Schema(implementation = TraineeProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    
    @PutMapping("/update")
    public ResponseEntity<TraineeProfileResponse> updateTrainee
            (@Valid @RequestBody TraineeUpdateRequest traineeUpdateRequest) {

        Trainee trainee = traineeService.selectTraineeProfileByUsername(
                traineeUpdateRequest.getUsername()
        );

        updateTrainerData(trainee, traineeUpdateRequest);

        traineeService.updateTraineeProfile(trainee);

        return ResponseEntity.ok()
                .body(traineeApiMapper.toTraineeProfileResponse(trainee));
    }



    @Operation(summary = "Delete trainee", description = "Deletes a trainee profile by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainee deleted successfully",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Username is blank", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    
    @DeleteMapping
    public ResponseEntity<String> deleteTrainee(@NotBlank @RequestParam("username") String username) {

        traineeService.deleteTraineeProfileByUsername(username);

        return ResponseEntity.ok("Trainee was deleted successfully!");
    }


    @Operation(summary = "Get unassigned active trainers", description = "Returns trainers not yet assigned to the given trainee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerSummaryResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    
    @GetMapping("/not-assigned-trainers")
    public ResponseEntity<List<TrainerSummaryResponse>> getNotAssignedActiveTrainers
            (@NotBlank @RequestParam("username") String username) {

        List<Trainer> trainerList = traineeService.getTrainersNotAssignedToTrainee(username);

        return ResponseEntity.ok().body(
                trainerList.stream()
                        .map(trainerApiMapper::toTrainerSummaryResponse)
                        .toList());
    }

    @Operation(summary = "Update trainee's trainer list", description = "Replaces the trainee's assigned trainers with the provided list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainer list updated",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerSummaryResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    
    @PutMapping("/update-trainers")
    public ResponseEntity<List<TrainerSummaryResponse>> updateTraineeTrainersList
            (@Valid @RequestBody TraineeTrainersUpdateRequest traineeTrainersUpdateRequest) {

        Trainee trainee = traineeService.
                selectTraineeProfileByUsername(traineeTrainersUpdateRequest.getTraineeUsername());

        traineeService.updateTraineeListOfTrainers(trainee.getId(), traineeTrainersUpdateRequest.getTrainerUsernames());


        return ResponseEntity.ok().body(traineeService.
                getTrainersAssignedToTrainee(traineeTrainersUpdateRequest.getTraineeUsername())
                .stream().
                map(trainerApiMapper::toTrainerSummaryResponse)
                .toList());
    }

    @Operation(summary = "Get trainee's trainings", description = "Returns filtered list of trainings for a trainee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trainings retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainingResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    
    @GetMapping("/trainings")
    public ResponseEntity<List<TrainingResponse>> getTraineeTrainingsList
            (@Valid @RequestBody TraineeTrainingsRequest traineeTrainingsRequest) {

        List<Training> trainings = traineeService.getTrainingsForTrainee(
                traineeTrainingsRequest.getUsername(),
                traineeTrainingsRequest.getFrom(),
                traineeTrainingsRequest.getTo(),
                traineeTrainingsRequest.getTrainerName(),
                traineeTrainingsRequest.getTrainingTypeRequest().getTrainingTypeName()
        );

        return ResponseEntity.ok().body(trainings.stream().map(trainingApiMapper::toTrainingResponse).toList());
    }


    @Operation(summary = "Activate or deactivate trainee", description = "Toggles the active status of a trainee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status changed successfully",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid path variable or param", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "Trainee not found", content = @Content)
    })
    
    @PatchMapping("/{username}/status")
    public ResponseEntity<String> changeTraineeActiveStatus
            (@NotBlank @PathVariable("username") String username,
             @NotNull @RequestParam("active") Boolean isActive) {

        Trainee trainee = traineeService.
                selectTraineeProfileByUsername(username);

        if(isActive) {
            traineeService.activateTraineeProfile(trainee.getId());
            return ResponseEntity.ok("trainee has been successfully activated!");
        }

        traineeService.deactivateTraineeProfile(trainee.getId());
        return ResponseEntity.ok("trainee has been successfully deactivated!");
    }

    /**
     * Takes update request data and sets appropriate fields of the trainee object
     * @param trainee object to be updated
     * @param traineeUpdateRequest data that is used to update trainee
     */
    private void updateTrainerData(Trainee trainee, TraineeUpdateRequest traineeUpdateRequest) {
        User user = trainee.getUser();

        user.setFirstName(traineeUpdateRequest.getFirstName());
        user.setLastName(traineeUpdateRequest.getLastName());


        if(traineeUpdateRequest.getDateOfBirth() != null)
            trainee.setDateOfBirth(traineeUpdateRequest.getDateOfBirth());

        if(traineeUpdateRequest.getAddress() != null)
            trainee.setAddress(traineeUpdateRequest.getAddress());

        if(traineeUpdateRequest.getIsActive() != null)
            user.setActive(traineeUpdateRequest.getIsActive());
    }

}
