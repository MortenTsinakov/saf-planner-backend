package hr.adriaticanimation.saf_planner.dtos.label;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AttachLabelsToFragmentRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testAttachLabelsToFragmentRequestValid() {
        List<Long> labelIds = List.of(1L, 2L, 3L);
        Long fragmentId = 1L;
        AttachLabelsToFragmentRequest request = new AttachLabelsToFragmentRequest(labelIds, fragmentId);

        Set<ConstraintViolation<AttachLabelsToFragmentRequest>> constraintViolations = validator.validate(request);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void testAttachLabelToFragmentRequestLabelIdsIsNull() {
        Long fragmentId = 1L;
        AttachLabelsToFragmentRequest request = new AttachLabelsToFragmentRequest(null, fragmentId);

        Set<ConstraintViolation<AttachLabelsToFragmentRequest>> constraintViolations = validator.validate(request);
        assertTrue(constraintViolations.stream().anyMatch(validator -> validator.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class)));
    }

    @Test
    void testAttachLabelToFragmentRequestFragmentIdIsNull() {
        List<Long> labelids = List.of(1L, 2L, 3L);
        AttachLabelsToFragmentRequest request = new AttachLabelsToFragmentRequest(labelids, null);

        Set<ConstraintViolation<AttachLabelsToFragmentRequest>> constraintViolations = validator.validate(request);
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class)));
    }
}