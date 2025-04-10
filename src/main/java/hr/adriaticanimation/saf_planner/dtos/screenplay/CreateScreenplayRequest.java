package hr.adriaticanimation.saf_planner.dtos.screenplay;

import hr.adriaticanimation.saf_planner.entities.screenplay.ScreenplayContent;
import jakarta.validation.constraints.NotNull;

public record CreateScreenplayRequest(
        @NotNull(message = "Project id can't be null")
        Long projectId,
        @NotNull(message = "Screenplay content can't be null")
        ScreenplayContent content
) {}
