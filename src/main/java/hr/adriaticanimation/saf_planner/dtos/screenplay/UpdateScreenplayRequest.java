package hr.adriaticanimation.saf_planner.dtos.screenplay;

import hr.adriaticanimation.saf_planner.entities.screenplay.ScreenplayContent;

public record UpdateScreenplayRequest(
   Long id,
   ScreenplayContent content
) {}
