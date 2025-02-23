package hr.adriaticanimation.saf_planner.dtos.fragment;

import hr.adriaticanimation.saf_planner.dtos.label.LabelResponse;

import java.util.List;

public record FragmentResponse(
        Long id,
        String shortDescription,
        String longDescription,
        Integer durationInSeconds,
        boolean onTimeline,
        Integer position,
        Long projectId,
        List<LabelResponse> labels,
        List<String> images
) {}
