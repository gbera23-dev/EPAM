import entities.*;
import facade.GymFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.util.*;


@ComponentScan(basePackages = {
        "persistence",
        "services",
        "facade",
        "BeanPostProcessor"
        })
@Configuration
public class ApplicationConfig {


    @Bean
    public Map<Long, Trainee> createTraineeDB() {
        return new HashMap<>();
    }

    @Bean
    public Map<Long, Trainer> createTrainerDB() {
        return new HashMap<>();
    }

    @Bean
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

}
