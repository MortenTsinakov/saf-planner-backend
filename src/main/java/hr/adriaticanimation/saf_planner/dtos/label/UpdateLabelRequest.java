package hr.adriaticanimation.saf_planner.dtos.label;

import hr.adriaticanimation.saf_planner.utils.validators.ValidHexValue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateLabelRequest(
        @NotNull
        Long labelId,
        @Size(max = 50)
        String description,
        @ValidHexValue
        String color
) {}