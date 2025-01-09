package hr.adriaticanimation.saf_planner.dtos.project;

import jakarta.validation.constraints.NotNull;

public record UpdateProjectDescriptionRequest(
        @NotNull
        Long projectId,
        String description
) {
}
