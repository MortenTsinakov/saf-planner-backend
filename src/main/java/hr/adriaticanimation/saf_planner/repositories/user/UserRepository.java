package hr.adriaticanimation.saf_planner.repositories.user;

import hr.adriaticanimation.saf_planner.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
