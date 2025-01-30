package hr.adriaticanimation.saf_planner.dtos.label;

public record AttachLabelToFragmentRequest(
        Long labelId,
        Long fragmentId
) {}
