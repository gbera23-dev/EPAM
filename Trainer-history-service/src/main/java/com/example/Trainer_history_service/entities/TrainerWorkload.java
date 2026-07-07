package com.example.Trainer_history_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @OneToMany(mappedBy="trainerWorkload", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MonthlySummary> monthlySummaryList;
}
