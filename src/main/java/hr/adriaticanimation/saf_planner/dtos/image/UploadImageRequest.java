package hr.adriaticanimation.saf_planner.dtos.image;

import hr.adriaticanimation.saf_planner.utils.validators.ValidImageFile;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UploadImageRequest(
        @ValidImageFile
        MultipartFile image,
        @NotNull(message = "Fragment id was not provided")
        Long fragmentId
) {}
