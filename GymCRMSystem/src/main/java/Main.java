import entities.Trainee;
import entities.User;
import facade.GymFacade;
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

        User user = new User(null, "John", "Nolan", null, null, false,
                null, null);

        Trainee trainee = new Trainee(null, LocalDate.now(), "tokutoku", user, null, null);

        gymFacade.createTrainee(trainee);
    }


}
