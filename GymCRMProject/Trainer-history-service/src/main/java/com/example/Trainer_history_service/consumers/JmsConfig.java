package com.example.Trainer_history_service.consumers;

import com.example.Trainer_history_service.dto.TrainerWorkloadBatchRequest;
import com.example.Trainer_history_service.dto.TrainerWorkloadRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JmsConfig {


    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());

        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();

        converter.setTargetType(MessageType.TEXT);

        converter.setTypeIdPropertyName("_type");

        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("trainerWorkloadRequest", TrainerWorkloadRequest.class);
        typeIdMappings.put("trainerWorkloadBatchRequest", TrainerWorkloadBatchRequest.class);

        converter.setTypeIdMappings(typeIdMappings);
        return converter;
    }

}
