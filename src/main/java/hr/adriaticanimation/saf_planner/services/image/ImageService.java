package hr.adriaticanimation.saf_planner.services.image;

import hr.adriaticanimation.saf_planner.dtos.image.UploadImageRequest;
import hr.adriaticanimation.saf_planner.dtos.image.UploadImageResponse;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ImageStorageException;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageService {

    private final Path uploadDirectory;
    private final AuthenticationService authenticationService;

    public ImageService(@Value("${uploads.directory}") String uploadDir, AuthenticationService authenticationService) {
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload directory", e);
        }
        this.authenticationService = authenticationService;
    }

    private String getFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.lastIndexOf(".") == -1) {
            throw new ImageStorageException("Invalid file format");
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    public ResponseEntity<UploadImageResponse> uploadImage(UploadImageRequest request) {
        try {
            MultipartFile image = request.image();
            Long fragmentId = request.fragmentId();

            User user = authenticationService.getUserFromSecurityContextHolder();
            Path userDir = uploadDirectory.resolve(String.valueOf(user.getId()));
            Files.createDirectories(userDir);

            String generatedFileName = UUID.randomUUID() + getFileExtension(image);

            Path targetPath = userDir.resolve(generatedFileName);
            Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // TODO: Create database table for storing image information. The fragmentId needs an index
            // TODO: Save image information (generated file name, fragment id) to database
            // TODO: Scale image down to some reasonable size

            UploadImageResponse response = new UploadImageResponse(
                    "Image saved",
                    generatedFileName
            );

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            throw new ImageStorageException("Failed to upload the image");
        }
    }

}
