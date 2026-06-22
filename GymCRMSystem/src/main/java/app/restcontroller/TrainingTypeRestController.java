package app.restcontroller;

import app.dto.api.response.TrainingTypeResponse;
import app.entities.TrainingType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import app.mappers.api.TrainingTypeApiMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import app.services.TrainingTypeService;

import java.util.List;

@RestController
@RequestMapping("api/training-types")
@Tag(name = "Training type management", description = "Operations for getting training types")
public class TrainingTypeRestController {

    private final TrainingTypeService trainingTypeService;
    private final TrainingTypeApiMapper trainingTypeApiMapper;


    public TrainingTypeRestController(TrainingTypeService trainingTypeService,
                                      TrainingTypeApiMapper trainingTypeApiMapper) {
        this.trainingTypeService = trainingTypeService;
        this.trainingTypeApiMapper = trainingTypeApiMapper;
    }

    @Operation(summary = "Get all training types", description = "Returns a list of all available training types")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Training types retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainingTypeResponse.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    
    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> getTrainingTypes() {

        List<TrainingType> trainingTypes = trainingTypeService.getTrainingTypes();

        return ResponseEntity.ok().body(
                trainingTypes.stream().map(trainingTypeApiMapper::toTrainingTypeResponse).toList()
        );
    }

}
