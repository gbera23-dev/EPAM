package app.clients;

import app.dto.api.request.TrainerWorkloadRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TrainerHistoryServiceFallback implements TrainerHistoryServiceClient{
    @Override
    public ResponseEntity<String> updateTrainerWorkload(TrainerWorkloadRequest trainerWorkloadRequest,
                                                        String authorizationHeader) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).
                body("Request timed out, could not send update trainer request");
    }

    @Override
    public ResponseEntity<String> updateTrainersWorkloadInBatch(List<TrainerWorkloadRequest> trainerWorkloadRequests,
                                                                String authorizationHeader) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).
                body("Request timed out, could not send update trainers batch request");    }

    @Override
    public ResponseEntity<Integer> getTrainerHours(String username, LocalDate localDate,
                                                   String authorizationHeader) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(-1);
    }
}
