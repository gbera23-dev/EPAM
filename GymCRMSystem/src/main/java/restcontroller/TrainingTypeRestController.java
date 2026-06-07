package restcontroller;

import dto.api.response.TrainingTypeResponse;
import entities.TrainingType;
import mappers.api.TrainingTypeApiMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.TrainingTypeService;

import java.util.List;

@RestController
@RequestMapping("api/training-types")
public class TrainingTypeRestController {

    private final TrainingTypeService trainingTypeService;
    private final TrainingTypeApiMapper trainingTypeApiMapper;


    public TrainingTypeRestController(TrainingTypeService trainingTypeService,
                                      TrainingTypeApiMapper trainingTypeApiMapper) {
        this.trainingTypeService = trainingTypeService;
        this.trainingTypeApiMapper = trainingTypeApiMapper;
    }

    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> getTrainingTypes() {

        List<TrainingType> trainingTypes = trainingTypeService.getTrainingTypes();

        return ResponseEntity.ok().body(
                trainingTypes.stream().map(trainingTypeApiMapper::toTrainingTypeResponse).toList()
        );
    }

}
