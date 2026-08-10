package org.example.integration.cucumber;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.time.Duration;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.Date;

public class TrainerHistoryServiceConnection {

    private static final String WORKLOADS = "trainer_workloads";
    private static final String SUMMARIES = "monthly_summaries";
    private static final MongoClient CLIENT = MongoClients.create(Config.MONGO_URI);

    private final MongoDatabase database = CLIENT.getDatabase(Config.MONGO_DATABASE);

    public int hoursFor(String trainerUsername, YearMonth month) {
        Document workload = workloadOf(trainerUsername);
        if (workload == null) {
            return 0;
        }

        for (Document summary : database.getCollection(SUMMARIES)
                .find(Filters.eq("trainerWorkload.$id", workload.get("_id")))) {
            if (isSameMonth(summary.getDate("date"), month)) {
                Integer duration = summary.getInteger("duration");
                return duration == null ? 0 : duration;
            }
        }
        return 0;
    }

    public Document workloadOf(String trainerUsername) {
        return database.getCollection(WORKLOADS)
                .find(Filters.eq("username", trainerUsername))
                .first();
    }

    private boolean isSameMonth(Date stored, YearMonth month) {
        if (stored == null) {
            return false;
        }
        Instant monthStart = month.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        return Math.abs(Duration.between(monthStart, stored.toInstant()).toHours()) <= 24;
    }
}