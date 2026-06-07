package restcontroller;

import annotations.AuthRequired;
import dto.api.request.TrainerRegistrationRequest;
import dto.api.request.TrainerTrainingsRequest;
import dto.api.request.TrainerUpdateRequest;
import dto.api.response.TrainerProfileResponse;
import dto.api.response.TrainingResponse;
import dto.api.response.UserCredentialsResponse;
import entities.Trainee;
import entities.Trainer;
import entities.Training;
import entities.TrainingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mappers.api.TrainerApiMapper;
import mappers.api.TrainingApiMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.TrainerService;
import services.TrainingTypeService;

import java.util.List;

@RestController
@RequestMapping("api/trainer")
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

    @PostMapping("/register")
    public ResponseEntity<UserCredentialsResponse> registerTrainer
            (@RequestBody TrainerRegistrationRequest trainerRegistrationRequest) {

        TrainingType trainingType = this.trainingTypeService.getTrainingTypeById(trainerRegistrationRequest.
                getSpecialization().getTrainingTypeId());

        Trainer trainer = trainerApiMapper.toTrainer(trainerRegistrationRequest, trainingType);

        trainerService.createTrainerProfile(trainer);

        return ResponseEntity.status(HttpStatus.CREATED).
                body(new UserCredentialsResponse(trainer.getUser().getUsername(), trainer.getUser().getPassword()));
    }

    @AuthRequired
    @GetMapping
    public ResponseEntity<TrainerProfileResponse> getTrainer
            (@NotBlank @RequestParam("username") String username) {

        Trainer trainer = trainerService.
                selectTrainerProfileByUsername(username);

        return ResponseEntity.ok().body(
                trainerApiMapper.toTrainerProfileResponse(trainer));
    }

    @AuthRequired
    @PutMapping("/update")
    public ResponseEntity<TrainerProfileResponse> updateTrainer
            (@Valid @RequestBody TrainerUpdateRequest trainerUpdateRequest) {

        Trainer trainer = trainerService.selectTrainerProfileByUsername(trainerUpdateRequest.getUsername());

        updateTrainerData(trainer, trainerUpdateRequest);

        trainerService.updateTrainerProfile(trainer);

        return ResponseEntity.ok().body(
                trainerApiMapper.toTrainerProfileResponse(trainer));
    }

    @AuthRequired
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


    @AuthRequired
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
