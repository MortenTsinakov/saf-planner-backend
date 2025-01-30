package hr.adriaticanimation.saf_planner.dtos.label;

import hr.adriaticanimation.saf_planner.utils.validators.ValidHexValue;
import lombok.Getter;

@Getter
public class UpdateLabelColorRequest extends UpdateLabelRequest {
    @ValidHexValue
    private String color;
}
