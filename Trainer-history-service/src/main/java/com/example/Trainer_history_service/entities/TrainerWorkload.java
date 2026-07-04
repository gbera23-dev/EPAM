package com.example.Trainer_history_service.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class TrainerWorkload {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="TrainerWorkload_id")
    private Long id;
    @Column(name="username")
    private String username;
    @Column(name="first_name")
    private String firstName;
    @Column(name="last_name")
    private String lastName;
    @Column(name="status")
    private boolean active;

    @OneToMany
    private List<MonthlySummery> monthlySummeryList;
}
