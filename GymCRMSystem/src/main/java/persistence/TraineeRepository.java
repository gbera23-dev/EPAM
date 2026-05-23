package persistence;

import entities.Trainee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository("TraineeRepository")
public interface TraineeRepository extends JpaRepository<Trainee, Long>{
}

