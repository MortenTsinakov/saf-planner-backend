package hr.adriaticanimation.saf_planner.repositories.label;

import hr.adriaticanimation.saf_planner.entities.label.LabelInFragment;
import hr.adriaticanimation.saf_planner.entities.label.LabelInFragmentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelInFragmentRepository extends JpaRepository<LabelInFragment, Long> {
    void deleteById(LabelInFragmentId id);
}
