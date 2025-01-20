package hr.adriaticanimation.saf_planner.dtos.fragment;

import jakarta.validation.constraints.Min;

public record MoveFragmentRequest(
        long fragmentId,
        @Min(value = 1, message = "New position has to be greater than 0")
        int newPosition
) {}
