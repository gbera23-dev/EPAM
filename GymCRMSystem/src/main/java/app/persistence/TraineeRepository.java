package app.persistence;

import app.entities.Trainee;
import app.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

@Repository("TraineeRepository")
public interface TraineeRepository extends JpaRepository<Trainee, Long>{

    @Query("SELECT t FROM Trainee t " +
            "JOIN FETCH t.user u " +
            "LEFT JOIN FETCH t.trainers tr " +
            "WHERE u.username = :username")
    Trainee findByUserUsername(@Param("username") String username);
}

