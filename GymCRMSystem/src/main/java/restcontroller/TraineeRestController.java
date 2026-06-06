package restcontroller;

import dto.api.request.TraineeRegistrationRequest;
import dto.api.request.TraineeUpdateRequest;
import dto.api.response.TraineeProfileResponse;
import dto.api.response.UserCredentialsResponse;
import entities.Trainee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import mappers.api.TraineeApiMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.AuthService;
import services.TraineeService;

@RestController
@RequestMapping("api/trainee")
public class TraineeRestController {

    private final TraineeService traineeService;
    private final AuthService authService;
    private final TraineeApiMapper traineeApiMapper;

    public TraineeRestController(TraineeService traineeService, AuthService authService,
                                 TraineeApiMapper traineeApiMapper) {
        this.traineeService = traineeService;
        this.authService = authService;
        this.traineeApiMapper = traineeApiMapper;
    }


    @PostMapping("/register")
    public ResponseEntity<UserCredentialsResponse> registerTrainee
            (@Valid @RequestBody TraineeRegistrationRequest traineeRegistrationRequest) {
        Trainee trainee = traineeApiMapper.toTrainee(traineeRegistrationRequest);

        traineeService.createTraineeProfile(trainee);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserCredentialsResponse(trainee.getUser().getUsername(), trainee.getUser().getPassword()));
    }

    @GetMapping("")
    public ResponseEntity<TraineeProfileResponse> getTrainee(@NotBlank @RequestParam String username) {

        Trainee trainee = traineeService.selectTraineeProfileByUsername(username);

        return ResponseEntity.ok().body(traineeApiMapper.toTraineeProfileResponse(trainee));
    }


    @PutMapping("/update")
    public ResponseEntity<TraineeProfileResponse> updateTrainee
            (@Valid @RequestBody TraineeUpdateRequest traineeUpdateRequest) {

        Trainee trainee = traineeService.selectTraineeProfileByUsername(
                traineeUpdateRequest.getUsername()
        );

        traineeService.updateTraineeProfile(trainee);

        return ResponseEntity.ok()
                .body(traineeApiMapper.toTraineeProfileResponse(trainee));
    }

    @DeleteMapping("")
    public ResponseEntity<String> deleteTrainee(@NotBlank @RequestParam String username) {

        traineeService.deleteTraineeProfileByUsername(username);

        return ResponseEntity.ok("Trainee was deleted successfully!");
    }


}
