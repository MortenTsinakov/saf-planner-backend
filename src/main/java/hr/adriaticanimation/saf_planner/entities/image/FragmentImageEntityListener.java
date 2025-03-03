package hr.adriaticanimation.saf_planner.entities.image;

import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ImageException;
import jakarta.persistence.PreRemove;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FragmentImageEntityListener {

    private final Path uploadDirectory;

    public FragmentImageEntityListener(@Value("${uploads.directory}") String uploadDir) {
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PreRemove
    public void preRemove(FragmentImage image) {
        try {
            Path path = uploadDirectory
                    .resolve(String.valueOf(image.getOwner().getId()))
                    .resolve(image.getId() + "." + image.getFileExtension());
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new ImageException("Image could not be deleted");
        }
    }
}
