package hr.adriaticanimation.saf_planner.dtos.label;

import hr.adriaticanimation.saf_planner.utils.validators.ValidHexValue;
import jakarta.validation.constraints.Size;

public record UpdateLabelRequest(
        @Size(max = 50)
        String description,
        @ValidHexValue
        String color
) {}