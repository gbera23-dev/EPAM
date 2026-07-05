package app.config;


import app.clients.TrainerHistoryServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {

    @Bean
    public TrainerHistoryServiceClient trainerHistoryServiceClient(@Value("${microservice.TrainerHistoryService.URI}")
                                                                   String uri) {
        RestClient restClient = RestClient.builder()
                .baseUrl(uri)
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return factory.createClient(TrainerHistoryServiceClient.class);
    }
}
