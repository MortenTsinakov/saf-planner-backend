package hr.adriaticanimation.saf_planner.dtos.authentication;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignUpRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidSignUpRequest() {
        SignUpRequest signUpRequest = new SignUpRequest(
                "email@email.com",
                "John",
                "Doe",
                "password"
        );

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(signUpRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testWrongEmailFormat() {
        SignUpRequest signUpRequest = new SignUpRequest(
                "email",
                "John",
                "Doe",
                "password"
        );

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(signUpRequest);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(
                        v -> v.getConstraintDescriptor()
                                .getAnnotation()
                                .annotationType()
                                .equals(Email.class)
                )
        );
    }

    @Test
    void testEmailIsNull() {
        SignUpRequest signUpRequest = new SignUpRequest(
                null,
                "John",
                "Doe",
                "password"
        );

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(signUpRequest);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(
                        v -> v.getConstraintDescriptor()
                                .getAnnotation()
                                .annotationType()
                                .equals(NotBlank.class)
                )
        );
    }

    @Test
    void testEmailIsEmpty() {
        SignUpRequest signUpRequest = new SignUpRequest(
                "",
                "John",
                "Doe",
                "password"
        );

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(signUpRequest);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(
                        v -> v.getConstraintDescriptor()
                                .getAnnotation()
                                .annotationType()
                                .equals(NotBlank.class)
                )
        );
    }

    @Test
    void testFirstNameIsBlank() {
        SignUpRequest signUpRequest = new SignUpRequest(
                "email@email.com",
                "    ",
                "Doe",
                "password"
        );

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(signUpRequest);
        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> v.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(NotBlank.class))
        );
    }

    @Test
    void testLastNameIsBlank() {
        SignUpRequest signUpRequest = new SignUpRequest(
                "email@email.com",
                "John",
                "     ",
                "password"
        );

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(signUpRequest);

        assertFalse(violations.isEmpty());
        assertTrue(
                violations.stream().anyMatch(v -> v.getConstraintDescriptor()
                        .getAnnotation()
                        .annotationType()
                        .equals(NotBlank.class))
        );
    }

    @Test
    void testPasswordIsTooShort() {
        SignUpRequest signUpRequest = new SignUpRequest(
                "email@email.com",
                "John",
                "Doe",
                "pass"
        );

        Set<ConstraintViolation<SignUpRequest>> violations = validator.validate(signUpRequest);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(Size.class)));
    }
}