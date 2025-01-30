package hr.adriaticanimation.saf_planner.utils.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidHexValueValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidHexValue {

    String message() default "Invalid hex value";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
