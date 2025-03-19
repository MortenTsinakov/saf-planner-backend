package hr.adriaticanimation.saf_planner.dtos.project;

public record ShareProjectRequest(
        Long projectId,
        Long shareWithId
) {}
