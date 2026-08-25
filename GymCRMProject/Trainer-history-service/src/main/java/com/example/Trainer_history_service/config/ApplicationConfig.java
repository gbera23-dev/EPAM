package com.example.Trainer_history_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApplicationConfig {

    @Bean(name="LayerRegistry")
    public Map<String, String> layerRegistry() {
        Map<String, String> reg = new HashMap<>();
        reg.put("com.example.Trainer_history_service.services", "Service");
        reg.put("com.example.Trainer_history_service.repository", "Persistence");
        reg.put("com.example.Trainer_history_service.facade", "Facade");
        reg.put("com.example.Trainer_history_service.consumers", "Consumer");
        return reg;
    }

}
