package com.example.Trainer_history_service.repository;

import com.example.Trainer_history_service.entities.TrainerWorkload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainerWorkloadRepository extends JpaRepository<TrainerWorkload, Long> {

    Optional<TrainerWorkload> findByUsername(String username);

    boolean existsByUsername(String username);

}
