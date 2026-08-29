package com.example.Trainer_history_service.domain.repository;


import com.example.Trainer_history_service.aop.annotations.PersistenceLayer;
import com.example.Trainer_history_service.domain.documents.MonthlySummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

@PersistenceLayer
public interface MonthlySummaryRepository extends MongoRepository<MonthlySummary,Long> {

    @Query("{ 'trainerWorkload.$id' : ?#{#id}, 'date' : ?#{#date} }")
    Optional<MonthlySummary> findByTrainerWorkloadIdAndDate(@Param("id") String id, @Param("date") LocalDate date);

    @Query("{ 'id' : ?#{#id} }")
    @Update("{ '$set' : { 'duration' : ?#{#duration} } }")
    void updateDuration(@Param("id") String id, @Param("duration") Integer duration);
}