package persistence;

import entities.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("TrainerRepository")
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
}
