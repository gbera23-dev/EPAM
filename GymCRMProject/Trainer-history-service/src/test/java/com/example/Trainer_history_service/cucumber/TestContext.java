package com.example.Trainer_history_service.cucumber;

import io.cucumber.spring.ScenarioScope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestContext {

    private Exception lastException;
    private Integer returnedHours;
    private String token;
    private String extractedUsername;
    private Boolean tokenValid;
    private String transactionId;
}