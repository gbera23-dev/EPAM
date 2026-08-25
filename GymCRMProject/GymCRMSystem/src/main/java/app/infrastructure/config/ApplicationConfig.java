package app.infrastructure.config;

import app.aop.annotations.ClientLayer;
import app.aop.annotations.PersistenceLayer;
import app.aop.annotations.ServiceLayer;
import app.infrastructure.dto.TraineeDTO;
import app.infrastructure.dto.TrainerDTO;
import app.infrastructure.dto.TrainingDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration class, which allows us to provide beans to the application. Both ComponentScan and Java - based
 * registration(@Bean registration) is used to provide beans
 */
@Configuration
@EnableScheduling
public class ApplicationConfig {

    @Bean(name="TraineeStorage")
    public Map<Long, TraineeDTO> createTraineeDB() {
        return new ConcurrentHashMap<>();
    }

    @Bean(name="TrainerStorage")
    public Map<Long, TrainerDTO> createTrainerDB() {
        return new ConcurrentHashMap<>();
    }

    @Bean(name="TrainingStorage")
    public Map<Long, TrainingDTO> createTrainingDB() {
        return new ConcurrentHashMap<>();
    }

    @Bean(name="TrainerResource")
    public Resource trainerResource(@Value("${data.TrainerDataPath}") String path) {
        return new ClassPathResource(path);
    }

    @Bean(name="TraineeResource")
    public Resource traineeResource(@Value("${data.TraineeDataPath}") String path) {
        return new ClassPathResource(path);
    }

    @Bean(name="TrainingResource")
    public Resource trainingResource(@Value("${data.TrainingDataPath}") String path) {
        return new ClassPathResource(path);
    }

    @Bean(name="LayerRegistry")
    public Map<String, String> layerRegistry() {
        Map<String, String> reg = new HashMap<>();
        reg.put(ServiceLayer.class.getSimpleName(), "Service");
        reg.put(PersistenceLayer.class.getSimpleName(), "Persistence");
        reg.put(ClientLayer.class.getSimpleName(), "Client");
        return reg;
    }

}
