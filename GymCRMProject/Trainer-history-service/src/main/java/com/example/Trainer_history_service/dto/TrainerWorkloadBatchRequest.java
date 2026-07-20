package com.example.Trainer_history_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrainerWorkloadBatchRequest {
    private List<TrainerWorkloadRequest> trainerWorkloadRequestList;
}
