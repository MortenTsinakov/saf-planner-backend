package hr.adriaticanimation.saf_planner.repositories.user;

import hr.adriaticanimation.saf_planner.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query(value = "SELECT * FROM users " +
           "WHERE similarity(first_name, :searchTerm) > :threshold " +
           "OR similarity(last_name, :searchTerm) > :threshold " +
           "ORDER BY (similarity(first_name, :searchTerm) + similarity(last_name, :searchTerm)) DESC " +
           "LIMIT 20", nativeQuery = true)
    List<User> findByFullName(@Param("searchTerm") String searchTerm, @Param("threshold") double threshold);
}
