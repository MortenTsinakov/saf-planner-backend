package hr.adriaticanimation.saf_planner.repositories.project;

import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> getProjectsByOwner(User owner);
    Optional<Project> getProjectByIdAndOwner(Long id, User owner);
}
