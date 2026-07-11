package app.clients;

import app.dto.api.request.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "Trainer-history-service", url = "${microservice.TrainerHistoryService.URI}",
fallback = TrainerHistoryServiceFallback.class)
public interface TrainerHistoryServiceClient {

    @PostMapping("/api/trainer")
    ResponseEntity<String> updateTrainerWorkload(
            @RequestBody TrainerWorkloadRequest trainerWorkloadRequest, @RequestHeader(HttpHeaders.AUTHORIZATION) String
            authorizationHeader);

    @PostMapping("/api/trainer/in-batch")
    ResponseEntity<String> updateTrainersWorkloadInBatch(
            @RequestBody List<TrainerWorkloadRequest> trainerWorkloadRequests,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader);

    @GetMapping("/api/trainer")
    ResponseEntity<Integer> getTrainerHours(
            @RequestParam("username") String username, @RequestParam("date") LocalDate localDate,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String
                    authorizationHeader);
}
