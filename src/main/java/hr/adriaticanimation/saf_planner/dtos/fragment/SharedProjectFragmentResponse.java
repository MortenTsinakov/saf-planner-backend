package hr.adriaticanimation.saf_planner.dtos.fragment;

import hr.adriaticanimation.saf_planner.dtos.comment.CommentResponse;
import hr.adriaticanimation.saf_planner.dtos.image.FragmentImageResponse;

import java.util.List;

public record SharedProjectFragmentResponse(
        Long id,
        String shortDescription,
        String longDescription,
        Integer durationInSeconds,
        Integer position,
        Long projectId,
        List<FragmentImageResponse> images,
        List<CommentResponse> comments
) {
}
