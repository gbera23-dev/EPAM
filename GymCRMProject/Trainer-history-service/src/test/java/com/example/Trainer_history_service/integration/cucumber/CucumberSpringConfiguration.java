package com.example.Trainer_history_service.integration.cucumber;

import com.example.Trainer_history_service.TrainerHistoryServiceApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes= TrainerHistoryServiceApplication.class)
@ActiveProfiles("test")

@Import({TestContext.class, JwtTokenFactory.class, TestUtils.class, DataTableConfigurer.class})
public class CucumberSpringConfiguration {
}
