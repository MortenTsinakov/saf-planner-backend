package hr.adriaticanimation.saf_planner.utils.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = OptionalNotBlankValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OptionalNotBlank {

    String message() default "Field cannot be blank";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
