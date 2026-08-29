package app.domain.persistence;

import app.aop.annotations.PersistenceLayer;
import app.domain.entities.Trainee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository("TraineeRepository")
@PersistenceLayer
public interface TraineeRepository extends JpaRepository<Trainee, Long>{

    @Query("SELECT t FROM Trainee t " +
            "JOIN FETCH t.user u " +
            "LEFT JOIN FETCH t.trainers tr " +
            "WHERE u.username = :username")
    Optional<Trainee> findByUserUsername(@Param("username") String username);
}

