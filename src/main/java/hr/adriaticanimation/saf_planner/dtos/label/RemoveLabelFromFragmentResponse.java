package hr.adriaticanimation.saf_planner.dtos.label;

public record RemoveLabelFromFragmentResponse(
        Long labelId,
        Long fragmentId,
        String message
) {}
