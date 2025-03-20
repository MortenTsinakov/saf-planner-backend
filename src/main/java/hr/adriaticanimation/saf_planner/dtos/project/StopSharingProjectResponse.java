package hr.adriaticanimation.saf_planner.dtos.project;

public record StopSharingProjectResponse(
        Long projectId,
        Long userId,
        String message
) {}
