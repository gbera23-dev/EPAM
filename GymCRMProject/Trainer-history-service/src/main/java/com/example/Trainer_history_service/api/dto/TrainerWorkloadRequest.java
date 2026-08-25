package com.example.Trainer_history_service.api.dto;

import com.example.Trainer_history_service.domain.documents.ActionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrainerWorkloadRequest {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private LocalDate trainingDate;
    private Integer duration;
    private ActionType actionType;
}
