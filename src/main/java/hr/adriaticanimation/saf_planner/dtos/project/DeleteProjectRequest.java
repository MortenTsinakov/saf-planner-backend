package hr.adriaticanimation.saf_planner.dtos.project;

import jakarta.validation.constraints.NotNull;

public record DeleteProjectRequest(
        @NotNull
        Long projectId
) {
}
