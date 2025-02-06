package hr.adriaticanimation.saf_planner.dtos.label;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AttachLabelsToFragmentRequest(
        @NotNull
        List<Long> labelIds,
        @NotNull
        Long fragmentId
) {}
