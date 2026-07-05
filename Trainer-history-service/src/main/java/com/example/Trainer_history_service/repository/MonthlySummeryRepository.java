package com.example.Trainer_history_service.repository;


import com.example.Trainer_history_service.entities.MonthlySummery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MonthlySummeryRepository extends JpaRepository<MonthlySummery,Long> {

    Optional<MonthlySummery> findByIdAndDate(Long id, LocalDate date);
}
