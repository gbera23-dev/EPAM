package app.domain.persistence;

import app.aop.annotations.PersistenceLayer;
import app.domain.entities.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@PersistenceLayer
public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {

    Optional<TrainingType> findById(long id);

}
