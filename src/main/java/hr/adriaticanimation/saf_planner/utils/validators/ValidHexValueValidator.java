package hr.adriaticanimation.saf_planner.utils.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidHexValueValidator implements ConstraintValidator<ValidHexValue, String> {

    private static final String HEX_REGEX = "^#([A-Fa-f0-9]{6})$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }
        return value.matches(HEX_REGEX);
    }
}
