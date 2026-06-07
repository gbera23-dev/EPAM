package restcontroller;

import dto.api.request.TrainerRegistrationRequest;
import dto.api.request.TrainerTrainingsRequest;
import dto.api.request.TrainerUpdateRequest;
import dto.api.response.TrainerProfileResponse;
import dto.api.response.TrainingResponse;
import dto.api.response.UserCredentialsResponse;
import entities.Trainee;
import entities.Trainer;
import entities.Training;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mappers.api.TrainerApiMapper;
import mappers.api.TrainingApiMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.TrainerService;

import java.util.List;

@RestController
@RequestMapping("api/trainer")
public class TrainerRestController {

    private final TrainerService trainerService;
    private final TrainerApiMapper trainerApiMapper;
    private final TrainingApiMapper trainingApiMapper;

    public TrainerRestController(TrainerService trainerService, TrainerApiMapper trainerApiMapper, TrainingApiMapper trainingApiMapper) {
        this.trainerService = trainerService;
        this.trainerApiMapper = trainerApiMapper;
        this.trainingApiMapper = trainingApiMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserCredentialsResponse> registerTrainer
            (@RequestBody TrainerRegistrationRequest trainerRegistrationRequest) {
        Trainer trainer = trainerApiMapper.toTrainer(trainerRegistrationRequest);

        trainerService.createTrainerProfile(trainer);

        return ResponseEntity.status(HttpStatus.CREATED).
                body(new UserCredentialsResponse(trainer.getUser().getUsername(), trainer.getUser().getPassword()));
    }

    @GetMapping
    public ResponseEntity<TrainerProfileResponse> getTrainer
            (@NotBlank @RequestParam("username") String username) {

        Trainer trainer = trainerService.
                selectTrainerProfileByUsername(username);

        return ResponseEntity.ok().body(
                trainerApiMapper.toTrainerProfileResponse(trainer));
    }

    @PutMapping("/update")
    public ResponseEntity<TrainerProfileResponse> updateTrainer
            (@Valid @RequestBody TrainerUpdateRequest trainerUpdateRequest) {

        Trainer trainer = trainerService.selectTrainerProfileByUsername(trainerUpdateRequest.getUsername());

        trainerService.updateTrainerProfile(trainer);

        return ResponseEntity.ok().body(
                trainerApiMapper.toTrainerProfileResponse(trainer));
    }

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

    @PatchMapping("/{username}/status")
    public ResponseEntity<String> changeTrainerActiveStatus
            (@PathVariable @NotBlank String username,
             @RequestParam @NotNull Boolean isActive) {

        Trainer trainer = trainerService.
                selectTrainerProfileByUsername(username);

        if(isActive) {
            trainerService.activateTrainerProfile(trainer.getId());
            return ResponseEntity.ok("trainer has been successfully activated!");
        }

        trainerService.deactivateTrainerProfile(trainer.getId());
        return ResponseEntity.ok("trainer has been successfully deactivated!");
    }

}
