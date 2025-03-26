package hr.adriaticanimation.saf_planner.services.image;

import hr.adriaticanimation.saf_planner.dtos.image.DeleteImageResponse;
import hr.adriaticanimation.saf_planner.dtos.image.FragmentImageResponse;
import hr.adriaticanimation.saf_planner.dtos.image.UploadImageRequest;
import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.image.FragmentImage;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ImageException;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ResourceNotFoundException;
import hr.adriaticanimation.saf_planner.mappers.image.FragmentImageMapper;
import hr.adriaticanimation.saf_planner.repositories.fragment.FragmentRepository;
import hr.adriaticanimation.saf_planner.repositories.image.FragmentImageRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import hr.adriaticanimation.saf_planner.services.project.SharedProjectService;
import org.apache.coyote.Response;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FragmentImageService {

    private final AuthenticationService authenticationService;
    private final FragmentImageRepository fragmentImageRepository;
    private final FragmentRepository fragmentRepository;
    private final FragmentImageMapper fragmentImageMapper;

    private final Path uploadDirectory;
    private final int MAX_DIMENSIONS = 750;
    private final SharedProjectService sharedProjectService;

    public FragmentImageService(@Value("${uploads.directory}") String uploadDir, AuthenticationService authenticationService, FragmentImageRepository fragmentImageRepository, FragmentRepository fragmentRepository, FragmentImageMapper fragmentImageMapper, SharedProjectService sharedProjectService) {
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload directory", e);
        }
        this.authenticationService = authenticationService;
        this.fragmentImageRepository = fragmentImageRepository;
        this.fragmentRepository = fragmentRepository;
        this.fragmentImageMapper = fragmentImageMapper;
        this.sharedProjectService = sharedProjectService;
    }

    /**
     * Attach an image to a fragment, save it to the filesystem and
     * add a reference to the database
     *
     * @param request - upload request containing an image and fragment id
     * @return - response containing the filename and fragment id
     */
    public ResponseEntity<FragmentImageResponse> uploadImage(UploadImageRequest request) {
        try {
            BufferedImage image = multipartFileToScaledImage(request.image());
            Fragment fragment = fragmentRepository.getFragmentById(request.fragmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fragment was not found"));

            FragmentImage fragmentImage = saveImage(image, fragment, request.description());
            FragmentImageResponse response = fragmentImageMapper.mapFragmentImageResponse(fragmentImage);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            throw new ImageException("Failed to upload the image");
        }
    }

    /**
     * Save image to filesystem and reference to it to the database.
     * Before saving scale the image if it's too big.
     *
     * @param image - image to be saved to filesystem
     * @param fragment - fragment that image will be attached to
     *
     * @return - reference to the image
     * @throws IOException - if filesystem operations fail
     */
    private FragmentImage saveImage(BufferedImage image, Fragment fragment, String description)  throws IOException{
        User user = authenticationService.getUserFromSecurityContextHolder();

        if (!fragment.getProject().getOwner().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Fragment with given id not found");
        }

        Path userDir = uploadDirectory.resolve(String.valueOf(user.getId()));
        Files.createDirectories(userDir);

        String uuid = UUID.randomUUID().toString();
        String fileExtension = "jpg";
        String generatedFileName = uuid + "." + fileExtension;

        Path targetPath = userDir.resolve(generatedFileName);
        ImageIO.write(image, fileExtension, targetPath.toFile());

        FragmentImage fragmentImage = FragmentImage.builder()
                .id(uuid)
                .fileExtension(fileExtension)
                .description(description)
                .fragment(fragment)
                .owner(user)
                .build();
        fragmentImage = fragmentImageRepository.save(fragmentImage);

        return fragmentImage;
    }

    /**
     * Turn MultipartFile to an image and scale it if necessary.
     * The MultipartFile is expected to be an image file.
     *
     * @param file - MultipartFile to be turned into an image
     * @return - BufferedImage object
     * @throws IOException - if reading the image fails
     */
    private BufferedImage multipartFileToScaledImage(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());

        int width = image.getWidth();
        int height = image.getHeight();

        if (width > MAX_DIMENSIONS || height > MAX_DIMENSIONS) {
            return Scalr.resize(
                    image, Scalr.Method.AUTOMATIC,
                    Scalr.Mode.AUTOMATIC,
                    MAX_DIMENSIONS,
                    MAX_DIMENSIONS,
                    Scalr.OP_ANTIALIAS
            );
        }

        return image;
    }

    /**
     * Fetch the requested image when the request is made by the project
     * owner
     *
     * @param imageFileName - image file name (with extension)
     * @return - Resource associated with the file name
     */
    public ResponseEntity<Resource> getImage(String imageFileName) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        return getImageResponse(user.getId(), imageFileName);
    }

    /**
     * Fetch the requested image when the request is made by
     * a user who the project is shared with
     *
     * @param projectId - id of the project of the fragment the image is attached to
     * @param imageFileName - name of the image file
     * @return - Resource associated with the file name
     */
    public ResponseEntity<Resource> getSharedImage(Long projectId, String imageFileName) {
        Project project = sharedProjectService.fetchProject(projectId);
        User projectOwner = project.getOwner();
        return getImageResponse(projectOwner.getId(), imageFileName);
    }

    /**
     *
     * @param ownerId - id of the user who owns the project of the fragment the image
     *                  is attached to
     * @param imageFileName - the image name
     * @return - response entity that contains the image file.
     */
    private ResponseEntity<Resource> getImageResponse(Long ownerId, String imageFileName) {
        try {
            Path path = uploadDirectory
                    .resolve(String.valueOf(ownerId))
                    .resolve(imageFileName)
                    .normalize();
            Resource image = new UrlResource(path.toUri());
            String contentType = Files.probeContentType(path);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageFileName + "\"")
                    .body(image);
        } catch (IOException | InvalidPathException e) {
            throw new ImageException("Image not found");
        }
    }

    /**
     * Delete image by name.
     *
     * @param imageFileName - image name (containing extension)
     * @return - response containing image name and success message
     */
    public ResponseEntity<DeleteImageResponse> deleteImage(String imageFileName) {
        String uuid = imageFileName.substring(0, imageFileName.lastIndexOf('.'));
        User user = authenticationService.getUserFromSecurityContextHolder();
        FragmentImage image = fragmentImageRepository.findFragmentImageById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Image was not found"));

        if (!image.getOwner().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Image with given id not found");
        }

        fragmentImageRepository.delete(image);

        DeleteImageResponse response = new DeleteImageResponse(
                imageFileName,
                "Image successfully deleted"
        );
        return ResponseEntity.ok(response);
    }
}
