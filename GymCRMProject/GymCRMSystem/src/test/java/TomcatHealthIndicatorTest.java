import app.healthIndicators.TomcatHealthIndicator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TomcatHealthIndicatorTest {

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private TomcatHealthIndicator indicator;

    @Test
    void testHealthTomcatRunningReturnsPort() {
        ServletWebServerApplicationContext servletContext = mock(ServletWebServerApplicationContext.class);
        TomcatWebServer tomcatWebServer = mock(TomcatWebServer.class);

        TomcatHealthIndicator localIndicator = new TomcatHealthIndicator(servletContext);

        when(servletContext.getWebServer()).thenReturn(tomcatWebServer);
        when(tomcatWebServer.getPort()).thenReturn(8080);

        Health health = localIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(8080, health.getDetails().get("port"));
    }

    @Test
    void testHealthContextCastFails() {
        when((Object) applicationContext).thenThrow(new ClassCastException("Not a ServletWebServerApplicationContext"));

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    void testHealthGetWebServerThrowsException() {
        ServletWebServerApplicationContext servletContext = mock(ServletWebServerApplicationContext.class);
        TomcatHealthIndicator localIndicator = new TomcatHealthIndicator(servletContext);

        when(servletContext.getWebServer()).thenThrow(new RuntimeException("Server not started"));

        Health health = localIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Server not started", health.getDetails().get("error"));
    }
}
