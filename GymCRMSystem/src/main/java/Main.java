import facade.GymFacade;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {


public static void main(String[] args) {
    ApplicationContext applicationContext =
            new AnnotationConfigApplicationContext(ApplicationConfig.class);


    GymFacade gymFacade = applicationContext.getBean(GymFacade.class);

    System.out.println(gymFacade.getTrainer(53L));

    System.out.println(gymFacade.getTrainee(43L));


    System.out.println(gymFacade.getTraining(12L));

}


}
