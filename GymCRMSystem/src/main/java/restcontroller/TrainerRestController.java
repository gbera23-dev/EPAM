package restcontroller;

import dto.api.request.TrainerRegistrationRequest;
import dto.api.request.TrainerUpdateRequest;
import dto.api.response.TrainerProfileResponse;
import dto.api.response.UserCredentialsResponse;
import entities.Trainer;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import mappers.api.TrainerApiMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.TrainerService;

@RestController
@RequestMapping("api/trainer")
public class TrainerRestController {

    private final TrainerService trainerService;
    private final TrainerApiMapper trainerApiMapper;

    public TrainerRestController(TrainerService trainerService, TrainerApiMapper trainerApiMapper) {
        this.trainerService = trainerService;
        this.trainerApiMapper = trainerApiMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserCredentialsResponse> registerTrainer
            (@RequestBody TrainerRegistrationRequest trainerRegistrationRequest) {
        Trainer trainer = trainerApiMapper.toTrainer(trainerRegistrationRequest);

        trainerService.createTrainerProfile(trainer);

        return ResponseEntity.status(HttpStatus.CREATED).
                body(new UserCredentialsResponse(trainer.getUser().getUsername(), trainer.getUser().getPassword()));
    }

    @GetMapping("")
    public ResponseEntity<TrainerProfileResponse> getTrainer
            (@NotBlank @RequestParam String username) {

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
}
