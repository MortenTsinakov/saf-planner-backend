package hr.adriaticanimation.saf_planner.dtos.project;

import hr.adriaticanimation.saf_planner.dtos.label.LabelResponse;
import hr.adriaticanimation.saf_planner.dtos.user.SharedWithResponse;

import java.sql.Timestamp;
import java.util.List;

public record ProjectResponse(
        Long id,
        String title,
        String description,
        Integer estimatedLengthInSeconds,
        Timestamp createdAt,
        Timestamp updatedAt,
        String owner,
        List<LabelResponse> labels,
        List<SharedWithResponse> sharedWith
) {}
