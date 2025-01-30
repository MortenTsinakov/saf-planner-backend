package hr.adriaticanimation.saf_planner.dtos.label;

import hr.adriaticanimation.saf_planner.utils.validators.ValidHexValue;
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

class UpdateLabelColorRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testUpdateLabelColorRequestValid() {
        UpdateLabelColorRequest request = new UpdateLabelColorRequest();
        request.setColor("#000abc");
        request.setLabelId(1L);
        Set<ConstraintViolation<UpdateLabelColorRequest>> constraintViolations = validator.validate(request);

        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void testUpdateLabelColorRequestLabelIdIsNull() {
        UpdateLabelColorRequest request = new UpdateLabelColorRequest();
        request.setLabelId(null);
        request.setColor("#000abc");

        Set<ConstraintViolation<UpdateLabelColorRequest>> constraintViolations = validator.validate(request);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class))
        );
    }

    @Test
    void testUpdateLabelColorRequestColorIsNull() {
        UpdateLabelColorRequest request = new UpdateLabelColorRequest();
        request.setLabelId(1L);
        request.setColor(null);
        Set<ConstraintViolation<UpdateLabelColorRequest>> constraintViolations = validator.validate(request);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(ValidHexValue.class))
        );
    }

    @Test
    void testUpdateLabelColorRequestInvalidHexValue() {
        UpdateLabelColorRequest request = new UpdateLabelColorRequest();
        request.setLabelId(1L);
        request.setColor("#ef01ijk");
        Set<ConstraintViolation<UpdateLabelColorRequest>> constraintViolations = validator.validate(request);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(ValidHexValue.class))
        );
    }
}