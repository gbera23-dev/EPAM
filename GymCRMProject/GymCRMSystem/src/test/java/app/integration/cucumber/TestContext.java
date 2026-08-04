package app.integration.cucumber;

import io.cucumber.spring.ScenarioScope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestContext {

    private Exception lastException;
    private String token;
    private String extractedUsername;
    private Boolean tokenValid;
    private String transactionId;
    private String responseBody;
    private HttpStatus status;
    private Long currentTrainingId;
}