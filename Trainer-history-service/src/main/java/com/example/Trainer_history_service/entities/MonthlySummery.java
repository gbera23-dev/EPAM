package com.example.Trainer_history_service.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class MonthlySummery {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="MonthlySummery_id")
    private Long id;
    @Column(name="date")
    private LocalDate date;
    @Column(name="duration")
    private Integer duration;

    @ManyToOne
    @JoinColumn(name="Trainer_workload_id")
    private TrainerWorkload trainerWorkload;
}
