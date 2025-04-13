package hr.adriaticanimation.saf_planner.services.screenplay;

import hr.adriaticanimation.saf_planner.services.screenplay.block_properties.BlockProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class ScreenplayLine {
    private String line;
    private BlockProperties properties;
}
