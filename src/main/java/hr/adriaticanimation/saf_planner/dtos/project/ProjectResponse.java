package hr.adriaticanimation.saf_planner.dtos.project;

import java.sql.Timestamp;

public record ProjectResponse(
        Long id,
        String title,
        String description,
        Integer estimatedLengthInSeconds,
        Timestamp createdAt,
        Timestamp updatedAt,
        String owner
) {}
