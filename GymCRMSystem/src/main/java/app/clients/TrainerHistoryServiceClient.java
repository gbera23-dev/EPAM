package app.clients;

import app.dto.api.request.TrainerHoursRequest;
import app.dto.api.request.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "Trainer-history-service", url = "${microservice.TrainerHistoryService.URI}")
public interface TrainerHistoryServiceClient {

    @PostMapping("/api/trainer")
    ResponseEntity<String> updateTrainerWorkload(
            @RequestBody TrainerWorkloadRequest trainerWorkloadRequest, @RequestHeader(HttpHeaders.AUTHORIZATION) String
            authorizationHeader);

    @GetMapping("/api/trainer")
    ResponseEntity<Integer> getTrainerHours(
            @RequestBody TrainerHoursRequest trainerHoursRequest, @RequestHeader(HttpHeaders.AUTHORIZATION) String
                    authorizationHeader);
}
