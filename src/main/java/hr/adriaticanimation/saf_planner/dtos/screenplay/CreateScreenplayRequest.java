package hr.adriaticanimation.saf_planner.dtos.screenplay;

import hr.adriaticanimation.saf_planner.entities.screenplay.ScreenplayContent;

public record CreateScreenplayRequest(
        Long projectId,
        ScreenplayContent content
) {}
