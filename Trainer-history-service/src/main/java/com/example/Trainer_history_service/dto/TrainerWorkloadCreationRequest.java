package com.example.Trainer_history_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrainerWorkloadCreationRequest {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
}
