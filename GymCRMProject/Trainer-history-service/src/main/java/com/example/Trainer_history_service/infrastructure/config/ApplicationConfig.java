package com.example.Trainer_history_service.infrastructure.config;

import com.example.Trainer_history_service.aop.annotations.ConsumerLayer;
import com.example.Trainer_history_service.aop.annotations.FacadeLayer;
import com.example.Trainer_history_service.aop.annotations.PersistenceLayer;
import com.example.Trainer_history_service.aop.annotations.ServiceLayer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApplicationConfig {

    @Bean(name="LayerRegistry")
    public Map<String, String> layerRegistry() {
        Map<String, String> reg = new HashMap<>();
        reg.put(ServiceLayer.class.getSimpleName(), "Service");
        reg.put(PersistenceLayer.class.getSimpleName(), "Persistence");
        reg.put(FacadeLayer.class.getSimpleName(), "Facade");
        reg.put(ConsumerLayer.class.getSimpleName(), "Consumer");
        return reg;
    }

}
