package hr.adriaticanimation.saf_planner.repositories.image;

import hr.adriaticanimation.saf_planner.entities.image.FragmentImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FragmentImageRepository extends JpaRepository<FragmentImage, String> {
    Optional<FragmentImage> findFragmentImageById(String id);
}
