package hr.adriaticanimation.saf_planner.dtos.label;

import hr.adriaticanimation.saf_planner.utils.validators.ValidHexValue;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateLabelRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    @Test
    void testCreateLabelRequestValid() {
        CreateLabelRequest request = new CreateLabelRequest(1L, "Description", "#000000");
        Set<ConstraintViolation<CreateLabelRequest>> constraintViolations = validator.validate(request);

        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void testCreateLabelRequestProjectIdIsNull() {
        CreateLabelRequest request = new CreateLabelRequest(null, "Description", "#000000");
        Set<ConstraintViolation<CreateLabelRequest>> constraintViolations = validator.validate(request);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class))
        );
    }

    @Test
    void testCreateLabelRequestDescriptionIsTooLong() {
        CreateLabelRequest request = new CreateLabelRequest(
                1L,
                "DescriptionDescriptionDescriptionDescriptionDescription",
                "#000000");
        Set<ConstraintViolation<CreateLabelRequest>> constraintViolations = validator.validate(request);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(Size.class))
        );
    }

    @Test
    void testCreateLabelRequestColorIsNull() {
        CreateLabelRequest request = new CreateLabelRequest(1L, "Description", null);
        Set<ConstraintViolation<CreateLabelRequest>> constraintViolations = validator.validate(request);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(ValidHexValue.class))
        );
    }

    @Test
    void testCreateLabelRequestColorIsThreeDigitHexValueFails() {
        CreateLabelRequest request = new CreateLabelRequest(1L, "Description", "#000");
        Set<ConstraintViolation<CreateLabelRequest>> constraintViolations = validator.validate(request);
        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(ValidHexValue.class)));
    }

    @Test
    void testCreateLabelRequestColorIsSixDigitHexValue() {
        CreateLabelRequest request = new CreateLabelRequest(1L, "Description", "#aB21cF");
        Set<ConstraintViolation<CreateLabelRequest>> constraintViolations = validator.validate(request);
        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void testCreateLabelRequestColorIsNotHexValue() {
        CreateLabelRequest request = new CreateLabelRequest(1L, "Description", "#ab%213");
        Set<ConstraintViolation<CreateLabelRequest>> constraintViolations = validator.validate(request);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(ValidHexValue.class))
        );
    }

    @Test
    void testCreateLabelRequestColorIsNotHexValueAlthoughAlphanumeric() {
        CreateLabelRequest request = new CreateLabelRequest(1L, "Description", "#ab21G3");
        Set<ConstraintViolation<CreateLabelRequest>> constraintViolations = validator.validate(request);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(ValidHexValue.class))
        );
    }
}