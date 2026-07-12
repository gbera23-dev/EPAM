package com.example.Trainer_history_service.repository;


import com.example.Trainer_history_service.entities.MonthlySummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MonthlySummeryRepository extends JpaRepository<MonthlySummary,Long> {

    Optional<MonthlySummary> findByTrainerWorkloadIdAndDate(Long id, LocalDate date);
}
