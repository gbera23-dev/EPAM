package app.healthIndicators;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;


    public DatabaseHealthIndicator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Health health() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            if (result != null && result == 1) {
                return Health.up()
                        .withDetail("database", "MySQL is up and responding")
                        .withDetail("latency", "Normal")
                        .build();
            }
            return Health.down().withDetail("error", "Unexpected query result").build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("database", "MySQL is unreachable")
                    .build();
        }
    }

}
