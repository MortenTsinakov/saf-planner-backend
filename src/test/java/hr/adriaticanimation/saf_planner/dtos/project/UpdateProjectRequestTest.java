package hr.adriaticanimation.saf_planner.dtos.project;

import hr.adriaticanimation.saf_planner.utils.validators.OptionalInterval;
import hr.adriaticanimation.saf_planner.utils.validators.OptionalLength;
import hr.adriaticanimation.saf_planner.utils.validators.OptionalNotBlank;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateProjectRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testUpdateProjectTitleRequest() {
        UpdateProjectRequest request = new UpdateProjectRequest(
                Optional.of("Title"),
                Optional.empty(),
                Optional.empty()
        );

        Set<ConstraintViolation<UpdateProjectRequest>> constraintViolations = validator.validate(request);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void testUpdateProjectRequestTitleIsEmpty() {
        UpdateProjectRequest request = new UpdateProjectRequest(
                Optional.of(""),
                Optional.empty(),
                Optional.empty()
        );

        Set<ConstraintViolation<UpdateProjectRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(OptionalNotBlank.class)));
    }

    @Test
    void testUpdateProjectRequestTitleIsBlank() {
        UpdateProjectRequest request = new UpdateProjectRequest(
                Optional.of("      "),
                Optional.empty(),
                Optional.empty()
        );

        Set<ConstraintViolation<UpdateProjectRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(OptionalNotBlank.class)));
    }

    @Test
    void testUpdateProjectTitleRequestTitleIsTooLong() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            stringBuilder.append((char) i);
        }
        String title = stringBuilder.toString();
        UpdateProjectRequest request = new UpdateProjectRequest(
                Optional.of(title),
                Optional.empty(),
                Optional.empty()
        );

        Set<ConstraintViolation<UpdateProjectRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(OptionalLength.class)));
    }

    @Test
    void testUpdateProjectEstimatedLengthRequestEstimatedLengthIsNegative() {
        UpdateProjectRequest request = new UpdateProjectRequest(
                Optional.empty(),
                Optional.empty(),
                Optional.of(-1)
        );

        Set<ConstraintViolation<UpdateProjectRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(OptionalInterval.class)
        ));
    }
}