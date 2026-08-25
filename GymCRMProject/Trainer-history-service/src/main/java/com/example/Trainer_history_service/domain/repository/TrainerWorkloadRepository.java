package com.example.Trainer_history_service.domain.repository;

import com.example.Trainer_history_service.aop.annotations.PersistenceLayer;
import com.example.Trainer_history_service.domain.documents.TrainerWorkload;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

@PersistenceLayer
public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkload, Long> {

    Optional<TrainerWorkload> findByUsername(String username);

    boolean existsByUsername(String username);

}