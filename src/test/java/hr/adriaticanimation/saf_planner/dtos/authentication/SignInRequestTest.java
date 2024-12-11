package hr.adriaticanimation.saf_planner.dtos.authentication;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignInRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testSignUpRequest() {
        SignInRequest signInRequest = new SignInRequest(
                "email@email.com",
                "password"
        );

        Set<ConstraintViolation<SignInRequest>> constraintViolations = validator.validate(signInRequest);

        assertTrue(constraintViolations.isEmpty());
    }

    @Test
    void testEmailInWrongFormat() {
        // @Email covers blank values as well
        SignInRequest signInRequest = new SignInRequest(
            "email",
            "password"
        );

        Set<ConstraintViolation<SignInRequest>> constraintViolations = validator.validate(signInRequest);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(Email.class))
        );
    }

    @Test
    void testEmailIsEmpty() {
        SignInRequest signInRequest = new SignInRequest(
                "",
                "password"
        );

        Set<ConstraintViolation<SignInRequest>> constraintViolations = validator.validate(signInRequest);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotBlank.class))
        );
    }

    @Test
    void testEmailIsNull() {
        SignInRequest signInRequest = new SignInRequest(
                null,
                "password"
        );

        Set<ConstraintViolation<SignInRequest>> constraintViolations = validator.validate(signInRequest);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotBlank.class))
        );
    }

    @Test
    void testPasswordIsEmpty() {
        // @NotEmpty also validates null values
        SignInRequest signInRequest = new SignInRequest(
            "email@email.com",
            ""
        );

        Set<ConstraintViolation<SignInRequest>> constraintViolations = validator.validate(signInRequest);

        assertFalse(constraintViolations.isEmpty());
        assertTrue(constraintViolations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotEmpty.class)));
    }
}