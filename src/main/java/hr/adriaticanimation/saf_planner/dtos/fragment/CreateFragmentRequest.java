package hr.adriaticanimation.saf_planner.dtos.fragment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateFragmentRequest(
        String shortDescription,
        String longDescription,
        @NotNull(message = "Fragment duration can't be null")
        @Min(value = 1, message = "Fragment duration has to be at least 1 second")
        Integer durationInSeconds,
        @NotNull(message = "Field for determining whether fragment is on timeline is missing")
        boolean onTimeline,
        @Min(value = 1, message = "Fragment position can't be less than 1")
        Integer position,
        @NotNull(message = "Project id is missing")
        Long projectId
) {}
