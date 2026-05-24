package persistence;

import entities.Trainee;
import entities.Trainer;
import entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("TrainerRepository")
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("SELECT u AS user FROM Trainee t JOIN t.user u " +
            "WHERE u.firstName = :#{#trainer.user.firstName} AND u.lastName = :#{#trainer.user.lastName}")
    List<User> getUsernameWithMaxNumberSuffix(@Param("trainer") Trainer trainer);

}
