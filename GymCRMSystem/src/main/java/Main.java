import config.ApplicationConfig;
import config.DataConfig;
import dto.TraineeDTO;
import facade.GymFacade;
import org.springframework.cglib.core.Local;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.LocalDate;


public class Main {

    /**
     * Main entry of the application
     * @param args Currently not used, but if application is extended, we might allow whoever starts the application
     *             to alter the application's behavior based on Program arguments
     */
    public static void main(String[] args) {
    ApplicationContext applicationContext =
            new AnnotationConfigApplicationContext(ApplicationConfig.class, DataConfig.class);


        GymFacade gymFacade = applicationContext.getBean(GymFacade.class);

        String username="mary.jones43", password="kDuAgxTAX5";

        gymFacade.loginUser(
            username, password
        );

        gymFacade.getTrainingsForTrainee("linda.garcia42",
                        null, null, null, null)
                .forEach(System.out::println);

        gymFacade.logoutUser();


        gymFacade.loginUser("james.rodriguez89", "sm5sUUHYog");


        TraineeDTO traineeDTO = gymFacade.getTraineeById(10);

        System.out.println(traineeDTO);

        gymFacade.logoutUser();
    }


}
