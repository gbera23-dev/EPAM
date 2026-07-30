package com.example.Trainer_history_service.repository;


import com.example.Trainer_history_service.documents.MonthlySummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.time.LocalDate;
import java.util.Optional;

public interface MonthlySummaryRepository extends MongoRepository<MonthlySummary,Long> {

    @Query("{ 'trainerWorkload.$id' : ?0, 'date' : ?1 }")
    Optional<MonthlySummary> findByTrainerWorkloadIdAndDate(String id, LocalDate date);

    @Query("{ 'id' : ?0 }")
    @Update("{ '$set' : { 'duration' : ?1 } }")
    void updateDuration(String id, Integer duration);
}