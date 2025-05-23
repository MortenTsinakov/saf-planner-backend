package hr.adriaticanimation.saf_planner.repositories.authentication;

import hr.adriaticanimation.saf_planner.entities.authentication.RefreshToken;
import hr.adriaticanimation.saf_planner.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser(User user);
    Optional<RefreshToken> findByToken(String token);

}
