package hr.adriaticanimation.saf_planner.dtos.project;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void testUpdateProjectTitleRequestIdIsNull() {
        UpdateProjectTitleRequest request = new UpdateProjectTitleRequest(
                null,
                "Title"
        );

        Set<ConstraintViolation<UpdateProjectTitleRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class)));
    }

    @Test
    void testUpdateProjectTitleRequest() {
        UpdateProjectTitleRequest request = new UpdateProjectTitleRequest(1L, "Title");

        Set<ConstraintViolation<UpdateProjectTitleRequest>> constraintViolations = validator.validate(request);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void testUpdateProjectTitleRequestTitleIsNull() {
        UpdateProjectTitleRequest request = new UpdateProjectTitleRequest(
                1L,
                null
        );

        Set<ConstraintViolation<UpdateProjectTitleRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotBlank.class)));
    }

    @Test
    void testUpdateProjectRequestTitleIsEmpty() {
        UpdateProjectTitleRequest request = new UpdateProjectTitleRequest(
                1L,
                ""
        );

        Set<ConstraintViolation<UpdateProjectTitleRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotBlank.class)));
    }

    @Test
    void testUpdateProjectRequestTitleIsBlank() {
        UpdateProjectTitleRequest request = new UpdateProjectTitleRequest(
                1L,
                "      "
        );

        Set<ConstraintViolation<UpdateProjectTitleRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotBlank.class)));
    }

    @Test
    void testUpdateProjectTitleRequestTitleIsTooLong() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            stringBuilder.append((char) i);
        }
        String title = stringBuilder.toString();
        UpdateProjectTitleRequest request = new UpdateProjectTitleRequest(
                1L,
                title
        );

        Set<ConstraintViolation<UpdateProjectTitleRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(Length.class)));
    }

    @Test
    void testUpdateProjectDescriptionRequestIdIsNull() {
        UpdateProjectDescriptionRequest request = new UpdateProjectDescriptionRequest(null, "Description");

        Set<ConstraintViolation<UpdateProjectDescriptionRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class)));
    }

    @Test
    void testUpdateProjectEstimatedLengthRequestIdIsNull() {
        UpdateProjectEstimatedLengthRequest request = new UpdateProjectEstimatedLengthRequest(null, 120);

        Set<ConstraintViolation<UpdateProjectEstimatedLengthRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class)));
    }

    @Test
    void testUpdateProjectEstimatedLengthRequestEstimatedLengthIsNegative() {
        UpdateProjectEstimatedLengthRequest request = new UpdateProjectEstimatedLengthRequest(
                1L,
                -1
        );

        Set<ConstraintViolation<UpdateProjectEstimatedLengthRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(Min.class)
        ));
    }
}