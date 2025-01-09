package hr.adriaticanimation.saf_planner.dtos.fragment;

import jakarta.validation.constraints.NotNull;

public record DeleteFragmentRequest(
        @NotNull
        Long fragmentId
) {}
