package hr.adriaticanimation.saf_planner.dtos.fragment;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateFragmentRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testUpdateFragmentRequestValid() {
        UpdateFragmentRequest request = new UpdateFragmentRequest(
                Optional.of("ShortDescription"),
                Optional.of("LongDescription"),
                Optional.of(10),
                Optional.of(true),
                Optional.of(1)
        );

        Set<ConstraintViolation<UpdateFragmentRequest>> constraintViolations = validator.validate(request);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void testUpdateFragmentRequestShortDescriptionIsNull() {
        UpdateFragmentRequest request = new UpdateFragmentRequest(
                null,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        Set<ConstraintViolation<UpdateFragmentRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class)));
    }

    @Test
    void testUpdateFragmentRequestLongDescriptionIsNull() {
        UpdateFragmentRequest request = new UpdateFragmentRequest(
                Optional.empty(),
                null,
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        Set<ConstraintViolation<UpdateFragmentRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class)));
    }

    @Test
    void testUpdateFragmentRequestDurationIsNull() {
        UpdateFragmentRequest request = new UpdateFragmentRequest(
                Optional.empty(),
                Optional.empty(),
                null,
                Optional.empty(),
                Optional.empty()
        );

        Set<ConstraintViolation<UpdateFragmentRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class)));
    }
    @Test
    void testUpdateFragmentRequestOnTimelineIsNull() {
        UpdateFragmentRequest request = new UpdateFragmentRequest(
                Optional.empty(),
                Optional.empty(),
                null,
                Optional.empty(),
                Optional.empty()
        );

        Set<ConstraintViolation<UpdateFragmentRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class)));
    }

    @Test
    void testUpdateFragmentRequestPositionIsNull() {
        UpdateFragmentRequest request = new UpdateFragmentRequest(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
        null
        );

        Set<ConstraintViolation<UpdateFragmentRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class)));
    }

    @Test
    void testUpdateFragmentRequestDurationIsLessThanOne() {
        UpdateFragmentRequest request = new UpdateFragmentRequest(
                Optional.empty(),
                Optional.empty(),
                Optional.of(0),
                Optional.empty(),
                Optional.empty()
        );

        Set<ConstraintViolation<UpdateFragmentRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(Min.class)));
    }

    @Test
    void testUpdateFragmentRequestPositionIsLessThanOne() {
        UpdateFragmentRequest request = new UpdateFragmentRequest(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(0)
        );

        Set<ConstraintViolation<UpdateFragmentRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(Min.class)));
    }
}