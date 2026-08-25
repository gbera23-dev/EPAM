package app.persistence;

import app.annotations.PersistenceLayer;
import app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
@PersistenceLayer
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findUsersByFirstNameAndLastName(String firstName, String lastName);
}
