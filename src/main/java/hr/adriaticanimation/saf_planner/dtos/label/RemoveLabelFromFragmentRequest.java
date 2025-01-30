package hr.adriaticanimation.saf_planner.dtos.label;

public record RemoveLabelFromFragmentRequest(
        Long labelId,
        Long fragmentId
) {
}
