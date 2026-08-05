package org.example.integration.cucumber;

import java.util.HashMap;
import java.util.Map;

public class TestContext {

    private String token;
    private String traineeUsername;
    private String secondTraineeUsername;
    private String trainerUsername;
    private String secondTrainerUsername;
    private String traineeFirstName;
    private String traineeLastName;
    private String trainerFirstName;
    private String trainerLastName;
    private String lastTrainingName;
    private int lastStatus;

    private final Map<String, String> trainingNamesByDate = new HashMap<>();

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTraineeUsername() {
        return traineeUsername;
    }

    public void setTraineeUsername(String traineeUsername) {
        this.traineeUsername = traineeUsername;
    }

    public String getSecondTraineeUsername() {
        return secondTraineeUsername;
    }

    public void setSecondTraineeUsername(String secondTraineeUsername) {
        this.secondTraineeUsername = secondTraineeUsername;
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public String getSecondTrainerUsername() {
        return secondTrainerUsername;
    }

    public void setSecondTrainerUsername(String secondTrainerUsername) {
        this.secondTrainerUsername = secondTrainerUsername;
    }

    public String getTraineeFirstName() {
        return traineeFirstName;
    }

    public void setTraineeFirstName(String traineeFirstName) {
        this.traineeFirstName = traineeFirstName;
    }

    public String getTraineeLastName() {
        return traineeLastName;
    }

    public void setTraineeLastName(String traineeLastName) {
        this.traineeLastName = traineeLastName;
    }

    public String getTrainerFirstName() {
        return trainerFirstName;
    }

    public void setTrainerFirstName(String trainerFirstName) {
        this.trainerFirstName = trainerFirstName;
    }

    public String getTrainerLastName() {
        return trainerLastName;
    }

    public void setTrainerLastName(String trainerLastName) {
        this.trainerLastName = trainerLastName;
    }

    public String getLastTrainingName() {
        return lastTrainingName;
    }

    public void setLastTrainingName(String lastTrainingName) {
        this.lastTrainingName = lastTrainingName;
    }

    public int getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(int lastStatus) {
        this.lastStatus = lastStatus;
    }

    public void rememberTraining(String date, String trainingName) {
        trainingNamesByDate.put(date, trainingName);
        lastTrainingName = trainingName;
    }

    public String trainingNameOn(String date) {
        String name = trainingNamesByDate.get(date);
        if (name == null) {
            throw new IllegalStateException("No training was added on " + date);
        }
        return name;
    }
}