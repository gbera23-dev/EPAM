import app.healthIndicators.DatabaseHealthIndicator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DatabaseHealthIndicatorTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private DatabaseHealthIndicator indicator;

    @Test
    void testHealthQueryReturnsOne() {
        when(jdbcTemplate.queryForObject(eq("SELECT 1"), eq(Integer.class))).thenReturn(1);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("MySQL is up and responding", health.getDetails().get("database"));
        assertEquals("Normal", health.getDetails().get("latency"));
    }

    @Test
    void testHealthQueryReturnsNull() {
        when(jdbcTemplate.queryForObject(eq("SELECT 1"), eq(Integer.class))).thenReturn(null);

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Unexpected query result", health.getDetails().get("error"));
    }

    @Test
    void testHealthQueryReturnsUnexpectedValue() {
        when(jdbcTemplate.queryForObject(eq("SELECT 1"), eq(Integer.class))).thenReturn(99);

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Unexpected query result", health.getDetails().get("error"));
    }

    @Test
    void testHealthQueryThrowsException() {
        when(jdbcTemplate.queryForObject(eq("SELECT 1"), eq(Integer.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("MySQL is unreachable", health.getDetails().get("database"));
    }
}
