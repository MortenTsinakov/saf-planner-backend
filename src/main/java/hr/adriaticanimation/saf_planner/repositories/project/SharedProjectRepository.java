package hr.adriaticanimation.saf_planner.repositories.project;

import hr.adriaticanimation.saf_planner.entities.project.SharedProject;
import hr.adriaticanimation.saf_planner.entities.project.SharedProjectId;
import hr.adriaticanimation.saf_planner.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SharedProjectRepository extends JpaRepository<SharedProject, Long> {
    Optional<SharedProject> getSharedProjectById(SharedProjectId sharedProjectId);
    List<SharedProject> findAllBySharedWith(User sharedWith);
}
