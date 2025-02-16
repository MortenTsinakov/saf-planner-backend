package hr.adriaticanimation.saf_planner.utils.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class OptionalIntervalValidator implements ConstraintValidator<OptionalInterval, Optional<Integer>> {

    private int min;
    private int max;

    @Override
    public void initialize(OptionalInterval constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Optional<Integer> value, ConstraintValidatorContext constraintValidatorContext) {

        if (value == null) {
            return true;
        }

        return value.map(v -> v >= min && v <= max).orElse(true);
    }
}
