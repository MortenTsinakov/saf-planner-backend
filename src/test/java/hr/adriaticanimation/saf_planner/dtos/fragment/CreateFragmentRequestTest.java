package hr.adriaticanimation.saf_planner.dtos.fragment;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateFragmentRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testCreateFragmentRequestSuccess() {
        CreateFragmentRequest request = new CreateFragmentRequest(
                "Short description",
                "Long description",
                15,
                true,
                1,
                1L
        );

        Set<ConstraintViolation<CreateFragmentRequest>> constraintViolations = validator.validate(request);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void testCreateFragmentRequestWithNullDurationFailsValidation() {
        CreateFragmentRequest request = new CreateFragmentRequest(
                "Short description",
                "Long description",
                null,
                true,
                1,
                1L
        );

        Set<ConstraintViolation<CreateFragmentRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class)));
    }

    @Test
    void testCreateFragmentRequestWithLowerThanMinDurationFailsValidation() {
        CreateFragmentRequest request = new CreateFragmentRequest(
                "Short description",
                "Long description",
                0,
                true,
                1,
                1L
        );

        Set<ConstraintViolation<CreateFragmentRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(Min.class)));
    }

    @Test
    void testCreateFragmentRequestLowerThanMinPositionFailsValidation() {
        CreateFragmentRequest request = new CreateFragmentRequest(
                "Short description",
                "Long description",
                15,
                false,
                0,
                1L
        );

        Set<ConstraintViolation<CreateFragmentRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(Min.class)));
    }

    @Test
    void testCreateFragmentRequestProjectIdMissingFailsValidation() {
        CreateFragmentRequest request = new CreateFragmentRequest(
                "Short description",
                "Long description",
                15,
                false,
                12,
                null
        );

        Set<ConstraintViolation<CreateFragmentRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class)));
    }
}