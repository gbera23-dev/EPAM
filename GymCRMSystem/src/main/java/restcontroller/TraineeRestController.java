package restcontroller;

import annotations.AuthRequired;
import dto.api.request.*;
import dto.api.response.*;
import entities.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mappers.api.TraineeApiMapper;
import mappers.api.TrainerApiMapper;
import mappers.api.TrainingApiMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.AuthService;
import services.TraineeService;
import services.TrainerService;

import java.util.List;

@RestController
@RequestMapping("api/trainee")
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


    @PostMapping("/register")
    public ResponseEntity<UserCredentialsResponse> registerTrainee
            (@Valid @RequestBody TraineeRegistrationRequest traineeRegistrationRequest) {
        Trainee trainee = traineeApiMapper.toTrainee(traineeRegistrationRequest);

        traineeService.createTraineeProfile(trainee);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserCredentialsResponse(trainee.getUser().getUsername(), trainee.getUser().getPassword()));
    }

    @AuthRequired
    @GetMapping
    public ResponseEntity<TraineeProfileResponse> getTrainee(@NotBlank @RequestParam("username") String username) {

        Trainee trainee = traineeService.selectTraineeProfileByUsername(username);

        return ResponseEntity.ok().body(traineeApiMapper.toTraineeProfileResponse(trainee));
    }

    @AuthRequired
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

    @AuthRequired
    @DeleteMapping
    public ResponseEntity<String> deleteTrainee(@NotBlank @RequestParam("username") String username) {

        traineeService.deleteTraineeProfileByUsername(username);

        return ResponseEntity.ok("Trainee was deleted successfully!");
    }

    @AuthRequired
    @GetMapping("/not-assigned-trainers")
    public ResponseEntity<List<TrainerSummaryResponse>> getNotAssignedActiveTrainers
            (@NotBlank @RequestParam("username") String username) {

        List<Trainer> trainerList = traineeService.getTrainersNotAssignedToTrainee(username);

        return ResponseEntity.ok().body(
                trainerList.stream()
                        .map(trainerApiMapper::toTrainerSummaryResponse)
                        .toList());
    }

    @AuthRequired
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


    @AuthRequired
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

    @AuthRequired
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
