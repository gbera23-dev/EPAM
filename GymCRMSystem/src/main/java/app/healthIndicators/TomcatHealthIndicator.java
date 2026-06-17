package app.healthIndicators;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class TomcatHealthIndicator implements HealthIndicator {

    private final ApplicationContext applicationContext;

    public TomcatHealthIndicator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Health health() {
        try {
            ServletWebServerApplicationContext context =
                    (ServletWebServerApplicationContext) applicationContext;

            TomcatWebServer tomcatWebServer =
                    (TomcatWebServer) context.getWebServer();

            int port = tomcatWebServer.getPort();

            return Health.up()
                    .withDetail("port", port)
                    .build();

        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}