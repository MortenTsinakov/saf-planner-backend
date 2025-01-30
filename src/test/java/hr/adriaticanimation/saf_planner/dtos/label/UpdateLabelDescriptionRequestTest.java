package hr.adriaticanimation.saf_planner.dtos.label;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateLabelDescriptionRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testUpdateLabelDescriptionRequestValid() {
        UpdateLabelDescriptionRequest request = new UpdateLabelDescriptionRequest();
        request.setDescription("description");
        request.setLabelId(1L);
        Set<ConstraintViolation<UpdateLabelDescriptionRequest>> constraintViolations = validator.validate(request);

        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void testUpdateLabelDescriptionRequestLabelIdIsNull() {
        UpdateLabelDescriptionRequest request = new UpdateLabelDescriptionRequest();
        request.setLabelId(null);
        request.setDescription("description");
        Set<ConstraintViolation<UpdateLabelDescriptionRequest>> constraintViolations = validator.validate(request);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class))
        );
    }

    @Test
    void testUpdateLabelDescriptionRequestDescriptionIsTooLong() {
        UpdateLabelDescriptionRequest request = new UpdateLabelDescriptionRequest();
        request.setDescription("description description description description description description");
        request.setLabelId(1L);
        Set<ConstraintViolation<UpdateLabelDescriptionRequest>> constraintViolations = validator.validate(request);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(Size.class))
        );
    }
}