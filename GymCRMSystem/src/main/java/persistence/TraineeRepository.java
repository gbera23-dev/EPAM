package persistence;

import entities.Trainee;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long>{
}

