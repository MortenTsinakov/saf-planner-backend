package hr.adriaticanimation.saf_planner.dtos.label;

import hr.adriaticanimation.saf_planner.utils.validators.ValidHexValue;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateLabelColorRequest extends UpdateLabelRequest {
    @ValidHexValue
    private String color;
}
