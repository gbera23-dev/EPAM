package persistence;


import entities.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("TrainingRepository")
public interface TrainingRepository extends JpaRepository<Training, Long> {
}
