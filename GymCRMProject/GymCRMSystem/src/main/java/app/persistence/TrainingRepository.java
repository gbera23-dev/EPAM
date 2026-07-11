package app.persistence;


import app.entities.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository("TrainingRepository")
public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("SELECT t FROM Training t WHERE t.trainee.user.username = :username " +
            "AND (:fromDate IS NULL OR t.date >= :fromDate) " +
            "AND (:toDate IS NULL OR t.date <= :toDate) " +
            "AND (:trainerName IS NULL OR t.trainer.user.username = :trainerName) " +
            "AND (:trainingTypeName IS NULL OR t.trainingType.name = :trainingTypeName)")
    List<Training> findTrainingsByTraineeCriteria(
            @Param("username") String username,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("trainerName") String trainerName,
            @Param("trainingTypeName") String trainingTypeName
    );

    @Query("SELECT t FROM Training t WHERE t.trainer.user.username = :username " +
            "AND (:fromDate IS NULL OR t.date >= :fromDate) " +
            "AND (:toDate IS NULL OR t.date <= :toDate) " +
            "AND (:traineeName IS NULL OR t.trainee.user.username = :traineeName) ")
    List<Training> findTrainingsByTrainerCriteria(
            @Param("username") String username,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("traineeName") String traineeName
    );

}
