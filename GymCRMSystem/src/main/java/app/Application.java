package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@ComponentScan(basePackages = {"app.config"})
@Configuration
@SpringBootConfiguration
@EnableAutoConfiguration
public class Application {

    /**
     * Main entry of the application
     * @param args Currently not used, but if application is extended, we might allow whoever starts the application
     *             to alter the application's behavior based on Program arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


}
