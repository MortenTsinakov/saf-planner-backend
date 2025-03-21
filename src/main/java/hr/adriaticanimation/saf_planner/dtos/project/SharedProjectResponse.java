package hr.adriaticanimation.saf_planner.dtos.project;

public record SharedProjectResponse(
        Long id,
        String title,
        String description,
        Integer estimatedLengthInSeconds,
        String owner
) {}
