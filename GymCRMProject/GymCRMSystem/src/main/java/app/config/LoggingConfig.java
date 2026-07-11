package app.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;

public class LoggingConfig implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {

            context.reset();

            ContextInitializer ci = new ContextInitializer(context);
            ci.autoConfig();

        } catch (JoranException e) {
            servletContext.log("Failed to initialize Logback synchronously", e);
        }
    }
}
