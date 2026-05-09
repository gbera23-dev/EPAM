import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import entities.*;
import facade.GymFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.*;


@ComponentScan(basePackages = {
        "persistence",
        "services",
        "facade",
        "BeanPostProcessor",
        "Builders",
        "Logging"
        })
@EnableAspectJAutoProxy
@Configuration
public class ApplicationConfig {


    @Bean(name="TraineeStorage")
    public Map<Long, Trainee> createTraineeDB() {
        return new HashMap<>();
    }

    @Bean(name="TrainerStorage")
    public Map<Long, Trainer> createTrainerDB() {
        return new HashMap<>();
    }

    @Bean(name="TrainingStorage")
    public Map<Long, Training> createTrainingDB() {
        return new HashMap<>();
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
        return new ObjectMapper();
    }

}
