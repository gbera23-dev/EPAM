package app.persistence;

import app.entities.Trainer;
import app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository("TrainerRepository")
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("SELECT t FROM Trainer t " +
            "JOIN FETCH t.user u " +
            "LEFT JOIN FETCH t.trainees tr LEFT JOIN FETCH t.trainingType " +
            "WHERE u.username = :username")
    Trainer findByUserUsername(@Param("username") String username);

    List<Trainer> findByUserUsernameIn(List<String> usernames);

    @Query("SELECT t FROM Trainer t " +
            "WHERE t.user.active = true " +
            "AND NOT EXISTS (" +
            "    SELECT tr FROM t.trainees tr " +
            "    WHERE tr.user.username = :username" +
            ")")
    List<Trainer> findTrainersNotAssignedToTrainee(@Param("username")String username);

    @Query("SELECT t FROM Trainer t " +
            " WHERE EXISTS (" +
            "    SELECT tr FROM t.trainees tr " +
            "    WHERE tr.user.username = :username" +
            ")")
    List<Trainer> findTrainersAssignedToTrainee(@Param("username")String username);
}
