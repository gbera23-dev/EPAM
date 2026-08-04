package com.example.Trainer_history_service.integration.cucumber;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerWorkloadDTO {
    private String username;
    private String firstName;
    private String lastName;
    private Boolean isActive;
}
