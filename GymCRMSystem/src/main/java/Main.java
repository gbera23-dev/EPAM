import entities.Trainee;
import entities.User;
import facade.GymFacade;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.Date;

import static java.time.LocalDate.now;

public class Main {


public static void main(String[] args) {
    ApplicationContext applicationContext =
            new AnnotationConfigApplicationContext(ApplicationConfig.class);


    GymFacade gymFacade = applicationContext.getBean(GymFacade.class);

    Trainee trainee = new Trainee(
       1L, Date.valueOf(now()),
            "Main street", new User("joni", "jojoni", null, null, true));

    gymFacade.createTrainee(trainee);

    Trainee retrievedTrainee = gymFacade.getTrainee(1L);

    System.out.println(retrievedTrainee);
}







}
