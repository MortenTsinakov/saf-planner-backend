package hr.adriaticanimation.saf_planner.dtos.fragment;

public record FragmentResponse(
        Long id,
        String shortDescription,
        String longDescription,
        Integer durationInSeconds,
        boolean onTimeline,
        Integer position,
        Long projectId
) {}
