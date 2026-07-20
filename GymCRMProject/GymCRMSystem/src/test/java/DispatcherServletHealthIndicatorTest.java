import app.healthIndicators.DispatcherServletHealthIndicator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispatcherServletHealthIndicatorTest {

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private DispatcherServletHealthIndicator indicator;

    @Test
    void testHealthWebApplicationContextInitialized() {
        DispatcherServlet servlet = mock(DispatcherServlet.class);
        WebApplicationContext wac = mock(WebApplicationContext.class);

        when(applicationContext.getBean(DispatcherServlet.class)).thenReturn(servlet);
        when(servlet.getServletName()).thenReturn("dispatcherServlet");
        when(servlet.getWebApplicationContext()).thenReturn(wac);
        when(wac.getBeansOfType(HandlerMapping.class)).thenReturn(Map.of("mapping1", mock(HandlerMapping.class)));

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("dispatcherServlet", health.getDetails().get("servlet"));
        assertEquals("initialized", health.getDetails().get("webApplicationContext"));
        assertEquals(1, health.getDetails().get("handlerMappings"));
    }

    @Test
    void testHealthWebApplicationContextNotInitialized() {
        DispatcherServlet servlet = mock(DispatcherServlet.class);

        when(applicationContext.getBean(DispatcherServlet.class)).thenReturn(servlet);
        when(servlet.getServletName()).thenReturn("dispatcherServlet");
        when(servlet.getWebApplicationContext()).thenReturn(null);

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("dispatcherServlet", health.getDetails().get("servlet"));
        assertEquals("WebApplicationContext not initialized", health.getDetails().get("error"));
    }

    @Test
    void testHealthGetBeanThrowsException() {
        when(applicationContext.getBean(DispatcherServlet.class))
                .thenThrow(new RuntimeException("Bean not found"));

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Bean not found", health.getDetails().get("error"));
    }
}
