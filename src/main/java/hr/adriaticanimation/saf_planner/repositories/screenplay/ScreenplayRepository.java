package hr.adriaticanimation.saf_planner.repositories.screenplay;

import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.screenplay.Screenplay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScreenplayRepository extends JpaRepository<Screenplay, Long> {
    Optional<Screenplay> getScreenplayByProject(Project project);
    Optional<Screenplay> getScreenplayById(Long id);
}
