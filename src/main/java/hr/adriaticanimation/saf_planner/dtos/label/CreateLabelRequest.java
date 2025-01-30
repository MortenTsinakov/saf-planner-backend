package hr.adriaticanimation.saf_planner.dtos.label;

import hr.adriaticanimation.saf_planner.utils.validators.ValidHexValue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateLabelRequest(
        @NotNull(message = "Missing project id in request")
        Long projectId,
        @Size(max = 50, message = "Description is too long")
        String description,
        @ValidHexValue
        String color
) {}
