import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dto.TraineeDTO;
import dto.TrainerDTO;
import dto.TrainingDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration class, which allows us to provide beans to the application. Both ComponentScan and Java - based
 * registration(@Bean registration) is used to provide beans
 */
@ComponentScan(basePackages = {
        "persistence",
        "services",
        "facade",
        "beanPostProcessor",
        "builders",
        "logging",
        "mappers",
        "database",
        "auth"
        })
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = "persistence")
@Configuration
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

    @Bean
    public static PropertySourcesPlaceholderConfigurer fileConfig() {
        PropertySourcesPlaceholderConfigurer Pspc =
                new PropertySourcesPlaceholderConfigurer();
        Pspc.setLocation(new ClassPathResource("application.properties"));
        return Pspc;
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

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

}
