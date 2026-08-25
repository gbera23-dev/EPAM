package app.persistence;

import app.annotations.PersistenceLayer;
import app.entities.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@PersistenceLayer
public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {

    Optional<TrainingType> findById(long id);

}
