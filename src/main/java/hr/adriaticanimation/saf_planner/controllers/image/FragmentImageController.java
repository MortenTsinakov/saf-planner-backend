package hr.adriaticanimation.saf_planner.controllers.image;

import hr.adriaticanimation.saf_planner.dtos.image.DeleteImageResponse;
import hr.adriaticanimation.saf_planner.dtos.image.FragmentImageResponse;
import hr.adriaticanimation.saf_planner.dtos.image.UploadImageRequest;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ImageException;
import hr.adriaticanimation.saf_planner.services.image.FragmentImageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class FragmentImageController {

    private final FragmentImageService fragmentImageService;

    @GetMapping(params = "image")
    @Operation(description = "Fetch an image by name")
    public ResponseEntity<Resource> getImage(@RequestParam("image") String image) {
        return fragmentImageService.getImage(image);
    }

    @GetMapping(value = "/shared", params = {"projectId", "image"})
    @Operation(description = "Fetch image for shared project by image name")
    public ResponseEntity<Resource> getSharedImage(@RequestParam("projectId") Long projectId, @RequestParam("image") String image) {
        return fragmentImageService.getSharedImage(projectId, image);
    }

    @PostMapping
    @Operation(description = "Attach an image to a fragment")
    public ResponseEntity<FragmentImageResponse> uploadImage(@Valid UploadImageRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult
                    .getAllErrors()
                    .getFirst()
                    .getDefaultMessage();
            throw new ImageException(message);
        }
        return fragmentImageService.uploadImage(request);
    }

    @DeleteMapping(params = "image")
    @Operation(description = "Delete image associated with a fragment by name")
    public ResponseEntity<DeleteImageResponse> deleteImage(@RequestParam("image") String image) {
        return fragmentImageService.deleteImage(image);
    }
}
