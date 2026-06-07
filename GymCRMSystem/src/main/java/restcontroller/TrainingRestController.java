package restcontroller;

import dto.api.request.TrainingRequest;
import dto.api.response.TrainingTypeResponse;
import entities.TrainingType;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.TrainingService;

@RestController
@RequestMapping("api/trainings")
public class TrainingRestController {

    private final TrainingService trainingService;

    public TrainingRestController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @PostMapping("")
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
