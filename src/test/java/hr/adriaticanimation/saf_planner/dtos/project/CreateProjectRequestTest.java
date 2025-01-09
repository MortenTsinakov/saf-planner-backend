package hr.adriaticanimation.saf_planner.dtos.project;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateProjectRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testCreateProjectRequest() {
        CreateProjectRequest request = new CreateProjectRequest(
                "Title of the project",
                "Description of the project",
                360
        );

        Set<ConstraintViolation<CreateProjectRequest>> constraintViolations = validator.validate(request);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void testCreateProjectTitleIsNull() {
        CreateProjectRequest request = new CreateProjectRequest(
                null,
                "Description of the project",
                360
        );

        Set<ConstraintViolation<CreateProjectRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                        .getAnnotation()
                .annotationType()
                .equals(NotBlank.class)));
    }

    @Test
    void testCreateProjectTitleIsEmpty() {
        CreateProjectRequest request = new CreateProjectRequest(
                "",
                "Description of the project",
                360
        );

        Set<ConstraintViolation<CreateProjectRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotBlank.class)));
    }

    @Test
    void testCreateProjectTitleTooLong() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            stringBuilder.append((char) i);
        }
        String title = stringBuilder.toString();
        CreateProjectRequest request = new CreateProjectRequest(
                title,
                "Project description",
                360
        );

        Set<ConstraintViolation<CreateProjectRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(Length.class)));
    }

    @Test
    void testCreateProjectEstimatedLengthIsNegative() {
        CreateProjectRequest request = new CreateProjectRequest(
                "Project title",
                "Project description",
                -12
        );

        Set<ConstraintViolation<CreateProjectRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(Min.class)));
    }

    @Test
    void testCreateProjectDescriptionIsNullIsAllowed() {
        CreateProjectRequest request = new CreateProjectRequest(
                "Title",
                null,
                360
        );

        Set<ConstraintViolation<CreateProjectRequest>> constraintViolations = validator.validate(request);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void testCreateProjectEstimatedLengthIsNullIsAllowed() {
        CreateProjectRequest request = new CreateProjectRequest(
                "Title",
                "Description",
                null
        );

        Set<ConstraintViolation<CreateProjectRequest>> constraintViolations = validator.validate(request);
        assertTrue(constraintViolations.isEmpty());
    }
}