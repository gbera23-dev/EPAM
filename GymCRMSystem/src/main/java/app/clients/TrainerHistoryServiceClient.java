package app.clients;

import app.dto.api.request.TrainerHoursRequest;
import app.dto.api.request.TrainerWorkloadRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/api/trainer")
public interface TrainerHistoryServiceClient {

    @PostExchange
    ResponseEntity<String> updateTrainerWorkload(
            @RequestBody TrainerWorkloadRequest trainerWorkloadRequest);

    @GetExchange
    ResponseEntity<Integer> getTrainerHours(
            @RequestBody TrainerHoursRequest trainerHoursRequest);
}
