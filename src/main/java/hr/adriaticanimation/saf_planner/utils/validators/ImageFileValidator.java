package hr.adriaticanimation.saf_planner.utils.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;

public class ImageFileValidator implements ConstraintValidator<ValidImageFile, MultipartFile> {

    private final int MAX_FILENAME_LENGTH = 255;
    private final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2mb
    private final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "webp", "svg", "svg+xml"};

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            context.buildConstraintViolationWithTemplate("Image file cannot be empty")
                    .addConstraintViolation();
            return false;
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.length() > MAX_FILENAME_LENGTH) {
            context.buildConstraintViolationWithTemplate("Filename is too long")
                    .addConstraintViolation();
            return false;
        }

        String fileExtension = getFileExtension(filename);
        if (!isValidExtension(fileExtension)) {
            context.buildConstraintViolationWithTemplate("File type is not supported")
                    .addConstraintViolation();
            return false;
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            context.buildConstraintViolationWithTemplate("File is too large")
                    .addConstraintViolation();
            return false;
        }

        if (!isValidMimeType(file)) {
            context.buildConstraintViolationWithTemplate("Invalid file type")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private boolean isValidExtension(String extension) {
        for (String allowedExtension : ALLOWED_EXTENSIONS) {
            if (allowedExtension.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidMimeType(MultipartFile file) {
        try {
            Metadata metadata = new Metadata();
            Detector detector = new DefaultDetector();
            MediaType mediaType = detector.detect(new BufferedInputStream(file.getInputStream()), metadata);
            for (String extension : ALLOWED_EXTENSIONS) {
                if (extension.equalsIgnoreCase(mediaType.getSubtype())) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}
