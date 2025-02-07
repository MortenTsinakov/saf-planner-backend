package hr.adriaticanimation.saf_planner.repositories.label;

import hr.adriaticanimation.saf_planner.entities.label.Label;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LabelRepository extends JpaRepository<Label, Long> {
    List<Label> findAllByProject(Project project);
    Optional<Label> getLabelById(Long id);
    List<Label> findLabelsByIdIsIn(List<Long> ids);
}
