package hr.adriaticanimation.saf_planner.dtos.label;

import hr.adriaticanimation.saf_planner.utils.validators.ValidHexValue;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateLabelRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try(ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testUpdateLabelRequestValid() {
        UpdateLabelRequest request = new UpdateLabelRequest(
                "Description",
                "#000000");
        Set<ConstraintViolation<UpdateLabelRequest>> constraintViolations = validator.validate(request);

        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void testUpdateLabelRequestDescriptionIsTooLong() {
        UpdateLabelRequest request = new UpdateLabelRequest("description description description description description description", "#000000");
        Set<ConstraintViolation<UpdateLabelRequest>> constraintViolations = validator.validate(request);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(Size.class))
        );
    }

    @Test
    void testUpdateLabelRequestColorIsNull() {
        UpdateLabelRequest request = new UpdateLabelRequest("Description", null);
        Set<ConstraintViolation<UpdateLabelRequest>> constraintViolations = validator.validate(request);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(ValidHexValue.class))
        );
    }

    @Test
    void testUpdateLabelColorRequestInvalidHexValue() {
        UpdateLabelRequest request = new UpdateLabelRequest( "Description", "#03fg1m");
        Set<ConstraintViolation<UpdateLabelRequest>> constraintViolations = validator.validate(request);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(ValidHexValue.class))
        );
    }
}