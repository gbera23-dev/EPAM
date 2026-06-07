package persistence;

import entities.Trainee;
import entities.Trainer;
import entities.Training;
import entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository("TrainerRepository")
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("SELECT u AS user FROM Trainee t JOIN t.user u " +
            "WHERE u.firstName = :#{#trainer.user.firstName} AND u.lastName = :#{#trainer.user.lastName}")
    List<User> getUsernameWithMaxNumberSuffix(@Param("trainer") Trainer trainer);


    @Query("SELECT t FROM Trainer t " +
            "JOIN FETCH t.user u " +
            "LEFT JOIN FETCH t.trainees tr LEFT JOIN FETCH t.trainingType " +
            "WHERE u.username = :username")
    Trainer findByUserUsername(@Param("username") String username);

    List<Trainer> findByUserUsernameIn(List<String> usernames);

    @Query("SELECT DISTINCT t FROM Trainer t " +
            "LEFT JOIN t.trainees tr WITH tr.user.username = :username " +
            "WHERE tr.id IS NULL")
    List<Trainer> findTrainersNotAssignedToTrainee(@Param("username")String username);
}
