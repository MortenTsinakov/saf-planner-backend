package hr.adriaticanimation.saf_planner.dtos.fragment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record UpdateFragmentRequest (
        @NotNull
        Optional<String> shortDescription,
        @NotNull
        Optional<String> longDescription,
        @NotNull
        Optional<@Min(value = 1, message = "Fragment's duration has to be at least 1 second") Integer> durationInSeconds,
        @NotNull
        Optional<Boolean> onTimeline,
        @NotNull
        Optional<@Min(value = 1, message = "New position has to be greater than 0") Integer> position
) {}