package hr.adriaticanimation.saf_planner.dtos.comment;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PostCommentRequestTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testPostCommentRequestSuccess() {
        PostCommentRequest request = new PostCommentRequest(1L, "Testing");
        Set<ConstraintViolation<PostCommentRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testPostCommentRequestFragmentIdIsNull() {
        PostCommentRequest request = new PostCommentRequest(null, "Testing");
        Set<ConstraintViolation<PostCommentRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotNull.class)));
    }

    @Test
    void testPostCommentRequestContentIsBlank() {
        PostCommentRequest request = new PostCommentRequest(1L, "   ");
        Set<ConstraintViolation<PostCommentRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getConstraintDescriptor()
                .getAnnotation()
                .annotationType()
                .equals(NotBlank.class)));
    }
}