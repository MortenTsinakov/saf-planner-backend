package hr.adriaticanimation.saf_planner.controllers.image;

import hr.adriaticanimation.saf_planner.dtos.image.UploadImageRequest;
import hr.adriaticanimation.saf_planner.dtos.image.UploadImageResponse;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ImageStorageException;
import hr.adriaticanimation.saf_planner.services.image.ImageService;
import hr.adriaticanimation.saf_planner.utils.validators.ValidImageFile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<UploadImageResponse> uploadImage(@Valid UploadImageRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult
                    .getAllErrors()
                    .getFirst()
                    .getDefaultMessage();
            throw new ImageStorageException(message);
        }
        return imageService.uploadImage(request);
    }
}
