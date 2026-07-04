package com.example.Trainer_history_service.entities;


import jakarta.persistence.*;

import java.time.LocalDate;

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
    @JoinColumn(name="trainerWorkload_id")
    private TrainerWorkload trainerWorkload;
}
