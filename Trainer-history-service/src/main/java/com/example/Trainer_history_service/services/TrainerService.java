package com.example.Trainer_history_service.services;

import java.time.LocalDate;

public interface TrainerService {


    void createNewWorkload(String username, String firstName, String lastName,
                           boolean isActive);

    Integer getTrainingHours(String username, LocalDate date);

    void addTrainingHours(String username, LocalDate date,
                             int hours);

    void deleteTrainingHours(String username, LocalDate date,
                             int hours);


}
