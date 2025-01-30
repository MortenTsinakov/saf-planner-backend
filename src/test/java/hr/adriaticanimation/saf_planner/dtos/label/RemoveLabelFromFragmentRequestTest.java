package hr.adriaticanimation.saf_planner.dtos.label;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RemoveLabelFromFragmentRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testRemoveLabelFromFragmentRequestValid() {
        RemoveLabelFromFragmentRequest request = new RemoveLabelFromFragmentRequest(1L, 1L);
        Set<ConstraintViolation<RemoveLabelFromFragmentRequest>> constraintViolations = validator.validate(request);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void testRemoveLabelFromFragmentRequestLabelIdIsNull() {
        RemoveLabelFromFragmentRequest request = new RemoveLabelFromFragmentRequest(null, 1L);
        Set<ConstraintViolation<RemoveLabelFromFragmentRequest>> constraintViolations = validator.validate(request);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class))
        );
    }

    @Test
    void testRemoveLabelFromFragmentRequestFragmentIdIsNull() {
        RemoveLabelFromFragmentRequest request = new RemoveLabelFromFragmentRequest(1L, null);
        Set<ConstraintViolation<RemoveLabelFromFragmentRequest>> constraintViolations = validator.validate(request);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class))
        );
    }
}