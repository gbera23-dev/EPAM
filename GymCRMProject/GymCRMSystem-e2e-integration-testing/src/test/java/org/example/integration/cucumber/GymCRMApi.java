package org.example.integration.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

public class GymCRMApi {

    public record Response(int status, String body) {
        public JsonNode json(ObjectMapper mapper) throws Exception {
            return mapper.readTree(body);
        }
    }

    public record Credentials(String username, String password) {
    }

    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Credentials registerTrainee() throws Exception {
        String firstName = randomName("Tee");
        String lastName = randomName("Doe");

        ObjectNode body = objectMapper.createObjectNode();
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.put("dateOfBirth", "1990-01-15");
        body.put("address", "12 Rustaveli");

        Response response = send("POST", "/api/trainee/register", null, body);
        expect(response, 201, "trainee registration");
        return credentialsFrom(response);
    }

    public Credentials registerTrainer(String token) throws Exception {
        String firstName = randomName("Ter");
        String lastName = randomName("Smith");

        ObjectNode body = objectMapper.createObjectNode();
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.putObject("specialization").put("trainingTypeId", anyTrainingTypeId(token));

        Response response = send("POST", "/api/trainer/register", null, body);
        expect(response, 201, "trainer registration");
        return credentialsFrom(response);
    }

    public String login(String username, String password) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("username", username);
        body.put("password", password);

        Response response = send("GET", "/api/user/login", null, body);
        expect(response, 200, "login");
        return response.json(objectMapper).get("jwt-token").textValue();
    }

    public long anyTrainingTypeId(String token) throws Exception {
        Response response = send("GET", "/api/training-types", token, null);
        expect(response, 200, "training type lookup");

        JsonNode types = response.json(objectMapper);
        if (!types.isArray() || types.isEmpty()) {
            throw new IllegalStateException("No training types exist in the main app");
        }
        JsonNode first = types.get(0);
        JsonNode id = first.hasNonNull("trainingTypeId") ? first.get("trainingTypeId") : first.get("id");
        return id.longValue();
    }

    public Response addTraining(String token, String traineeUsername, String trainerUsername,
                                String trainingName, String date, int duration) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("traineeUsername", traineeUsername);
        body.put("trainerUsername", trainerUsername);
        body.put("trainingName", trainingName);
        body.put("date", date);
        body.put("duration", duration);

        return send("POST", "/api/trainings", token, body);
    }

    public Response deleteTraining(String token, long trainingId) throws Exception {
        return send("DELETE", "/api/trainings/" + trainingId, token, null);
    }

    public Response deleteTrainee(String token, String username) throws Exception {
        return send("DELETE", "/api/trainee?username=" + username, token, null);
    }

    public long trainingIdOf(String token, String traineeUsername, String trainingName) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.put("username", traineeUsername);
        body.putNull("from");
        body.putNull("to");
        body.putNull("trainerName");
        body.putObject("trainingTypeRequest").putNull("trainingTypeName");

        Response response = send("GET", "/api/trainee/trainings", token, body);
        expect(response, 200, "training lookup");

        for (JsonNode training : response.json(objectMapper)) {
            if (matchesName(training, trainingName)) {
                JsonNode id = training.hasNonNull("id") ? training.get("id") : training.get("trainingId");
                if (id == null) {
                    throw new IllegalStateException(
                            "TrainingResponse carries no id field, cannot delete by name: " + training);
                }
                return id.longValue();
            }
        }
        throw new IllegalStateException("No training named " + trainingName + " for " + traineeUsername);
    }

    private boolean matchesName(JsonNode training, String trainingName) {
        return trainingName.equals(text(training, "trainingName")) || trainingName.equals(text(training, "name"));
    }

    private String text(JsonNode node, String field) {
        return node.hasNonNull(field) ? node.get(field).textValue() : null;
    }

    private Credentials credentialsFrom(Response response) throws Exception {
        JsonNode json = response.json(objectMapper);
        return new Credentials(json.get("username").textValue(), json.get("password").textValue());
    }

    private Response send(String method, String path, String token, ObjectNode body) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create(Config.MAIN_APP_URL + path))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json");

        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }

        HttpRequest.BodyPublisher publisher = body == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body));

        HttpResponse<String> response = httpClient.send(
                request.method(method, publisher).build(), HttpResponse.BodyHandlers.ofString());

        return new Response(response.statusCode(), response.body());
    }

    private void expect(Response response, int status, String what) {
        if (response.status() != status) {
            throw new IllegalStateException(
                    what + " failed with " + response.status() + ": " + response.body());
        }
    }

    private String randomName(String prefix) {
        StringBuilder name = new StringBuilder(prefix);
        for (int i = 0; i < 6; i++) {
            name.append((char) ('a' + ThreadLocalRandom.current().nextInt(26)));
        }
        return name.toString();
    }
}