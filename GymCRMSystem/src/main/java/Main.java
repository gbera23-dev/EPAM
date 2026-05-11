import facade.GymFacade;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    /**
     * Main entry of the application
     * @param args Currently not used, but if application is extended, we might allow whoever starts the application
     *             to alter the application's behavior based on Program arguments
     */
    public static void main(String[] args) {
    ApplicationContext applicationContext =
            new AnnotationConfigApplicationContext(ApplicationConfig.class);


    GymFacade gymFacade = applicationContext.getBean(GymFacade.class);

    System.out.println(gymFacade.getTrainer(53L));

    System.out.println(gymFacade.getTrainee(43L));


    System.out.println(gymFacade.getTraining(12L));

}


}
