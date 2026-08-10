package org.example.integration.cucumber;

import java.time.Duration;

public final class Config {

    public static final String MAIN_APP_URL =
            env("E2E_MAIN_APP_URL", "http://localhost:18080");

    public static final String MONGO_URI =
            env("E2E_MONGO_URI", "mongodb://admin:admin@localhost:27018/?authSource=admin");

    public static final String MONGO_DATABASE =
            env("E2E_MONGO_DATABASE", "trainer_history_db");

    public static final Duration MESSAGE_TIMEOUT =
            Duration.ofSeconds(Long.parseLong(env("E2E_MESSAGE_TIMEOUT_SECONDS", "20")));

    public static final Duration POLL_INTERVAL = Duration.ofMillis(250);

    private Config() {
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}