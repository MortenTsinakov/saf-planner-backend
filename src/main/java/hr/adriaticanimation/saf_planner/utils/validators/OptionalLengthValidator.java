package hr.adriaticanimation.saf_planner.utils.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class OptionalLengthValidator implements ConstraintValidator<OptionalLength, Optional<String>> {

    private int min;
    private int max;

    @Override
    public void initialize(OptionalLength constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Optional<String> value, ConstraintValidatorContext constraintValidatorContext) {

        if (value == null) {
            return true;
        }

        return value.map(s -> s.length() >= min && s.length() <= max).orElse(true);
    }
}
