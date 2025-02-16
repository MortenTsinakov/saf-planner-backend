package hr.adriaticanimation.saf_planner.dtos.fragment;

import hr.adriaticanimation.saf_planner.utils.validators.OptionalInterval;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record UpdateFragmentRequest (
        @NotNull
        Optional<String> shortDescription,
        @NotNull
        Optional<String> longDescription,
        @NotNull
        @OptionalInterval(min = 1, max = 3600, message = "Fragment duration has to be >= 1 and <= 3600 seconds")
        Optional<Integer> durationInSeconds,
        @NotNull
        Optional<Boolean> onTimeline,
        @NotNull
        @OptionalInterval(min = 1, message = "Fragment position has to be greater than 0")
        Optional<Integer> position
) {}