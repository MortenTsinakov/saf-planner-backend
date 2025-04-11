package hr.adriaticanimation.saf_planner.dtos.screenplay;

import hr.adriaticanimation.saf_planner.entities.screenplay.ScreenplayContent;
import jakarta.validation.constraints.NotNull;

public record UpdateScreenplayRequest(
        @NotNull(message = "Screenplay id can't be null")
        Long id,
        @NotNull(message = "Screenplay content can't be null")
        ScreenplayContent content
) {}
