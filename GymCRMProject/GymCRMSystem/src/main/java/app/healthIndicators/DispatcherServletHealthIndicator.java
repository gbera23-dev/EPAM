package app.healthIndicators;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;

@Component
public class DispatcherServletHealthIndicator implements HealthIndicator {

    private final ApplicationContext applicationContext;

    public DispatcherServletHealthIndicator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Health health() {
        try {
            DispatcherServlet dispatcherServlet = applicationContext
                    .getBean(DispatcherServlet.class);

            String servletName  = dispatcherServlet.getServletName();
            boolean initialized = dispatcherServlet.getWebApplicationContext() != null;

            if (!initialized) {
                return Health.down()
                        .withDetail("servlet", servletName)
                        .withDetail("error", "WebApplicationContext not initialized")
                        .build();
            }

            int handlerMappingCount = dispatcherServlet
                    .getWebApplicationContext()
                    .getBeansOfType(org.springframework.web.servlet.HandlerMapping.class)
                    .size();

            return Health.up()
                    .withDetail("servlet", servletName)
                    .withDetail("webApplicationContext", "initialized")
                    .withDetail("handlerMappings", handlerMappingCount)
                    .build();

        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}