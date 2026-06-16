package app;
import app.config.ApplicationConfig;
import app.config.DataConfig;
import app.config.WebConfig;
import app.tomcat.TomcatInitializer;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

@ComponentScan(basePackages = {"app.config"})
@Configuration
public class Application {

    /**
     * Main entry of the application
     * @param args Currently not used, but if application is extended, we might allow whoever starts the application
     *             to alter the application's behavior based on Program arguments
     */
    public static void main(String[] args) throws LifecycleException {

        AnnotationConfigWebApplicationContext springContext = new
                AnnotationConfigWebApplicationContext();

        springContext.register(WebConfig.class);
        springContext.register(DataConfig.class);
        springContext.register(ApplicationConfig.class);

        Tomcat tomcat = TomcatInitializer.configureTomcat(springContext);

        tomcat.start();
        tomcat.getServer().await();
    }


}
