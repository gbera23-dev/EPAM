package com.example.Trainer_history_service.dto;

import com.example.Trainer_history_service.entities.ActionType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadRequest {
    private String username;
    private String firstName;
    private String lastName;
    private boolean isActive;
    private LocalDate trainingDate;
    private Integer duration;
    private ActionType actionType;
}
