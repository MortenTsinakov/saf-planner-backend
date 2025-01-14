package hr.adriaticanimation.saf_planner.dtos.fragment;

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

class DeleteFragmentRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory();) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testDeleteFragmentRequestSuccess() {
        DeleteFragmentRequest request = new DeleteFragmentRequest(1L);

        Set<ConstraintViolation<DeleteFragmentRequest>> constraintViolations = validator.validate(request);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void testDeleteFragmentRequestMissingFragmentIdFailsValidation() {
        DeleteFragmentRequest request = new DeleteFragmentRequest(null);
        Set<ConstraintViolation<DeleteFragmentRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class)));
    }
}