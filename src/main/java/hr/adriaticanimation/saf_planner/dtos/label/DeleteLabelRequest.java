package hr.adriaticanimation.saf_planner.dtos.label;

import jakarta.validation.constraints.NotNull;

public record DeleteLabelRequest(
        @NotNull
        Long labelId
) {}
