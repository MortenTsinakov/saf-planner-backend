package hr.adriaticanimation.saf_planner.utils.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Optional;

public class OptionalNotBlankValidator implements ConstraintValidator<OptionalNotBlank, Optional<String>> {

    @Override
    public boolean isValid(Optional<String> value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        return value.map(s -> !s.isBlank()).orElse(true);
    }
}
