package persistence;

import entities.Trainee;
import entities.Training;
import entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

@Repository("TraineeRepository")
public interface TraineeRepository extends JpaRepository<Trainee, Long>{

    @Query("SELECT u AS user FROM Trainee t JOIN t.user u " +
            "WHERE u.firstName = :#{#trainee.user.firstName} AND u.lastName = :#{#trainee.user.lastName}")
    List<User> getUsernameWithMaxNumberSuffix(@Param("trainee") Trainee trainee);

    Trainee findByUserUsername(String username);
}

