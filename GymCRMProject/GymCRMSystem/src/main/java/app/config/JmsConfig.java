package app.config;

import app.dto.api.request.TrainerWorkloadBatchRequest;
import app.dto.api.request.TrainerWorkloadRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JmsConfig {


    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

        converter.setTargetType(MessageType.TEXT);

        converter.setTypeIdPropertyName("_type");

        objectMapper.registerModule(new JavaTimeModule());

        converter.setObjectMapper(objectMapper);

        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("trainerWorkloadRequest", TrainerWorkloadRequest.class);
        typeIdMappings.put("trainerWorkloadBatchRequest", TrainerWorkloadBatchRequest.class);

        converter.setTypeIdMappings(typeIdMappings);

        return converter;
    }

}
