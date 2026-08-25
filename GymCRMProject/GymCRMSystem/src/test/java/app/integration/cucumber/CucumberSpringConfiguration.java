package app.integration.cucumber;

import app.Application;
import app.messaging.microserviceCommunication.TrainerHistoryServiceCommunication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@SpringBootTest(classes= Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
    @MockitoBean
    TrainerHistoryServiceCommunication trainerHistoryServiceCommunication;
}
