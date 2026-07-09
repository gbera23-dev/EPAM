package app.config;

import app.dto.internal.TraineeDTO;
import app.dto.internal.TrainerDTO;
import app.dto.internal.TrainingDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;
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

}
