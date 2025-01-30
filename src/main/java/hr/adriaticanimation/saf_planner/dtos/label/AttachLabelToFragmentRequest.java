package hr.adriaticanimation.saf_planner.dtos.label;

import jakarta.validation.constraints.NotNull;

public record AttachLabelToFragmentRequest(
        @NotNull
        Long labelId,
        @NotNull
        Long fragmentId
) {}
