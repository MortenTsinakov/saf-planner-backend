package hr.adriaticanimation.saf_planner.dtos.label;

import jakarta.validation.constraints.NotNull;

public record RemoveLabelFromFragmentRequest(
        @NotNull
        Long labelId,
        @NotNull
        Long fragmentId
) {
}
