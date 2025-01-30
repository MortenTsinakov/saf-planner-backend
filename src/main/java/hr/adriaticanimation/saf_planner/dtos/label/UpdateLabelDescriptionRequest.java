package hr.adriaticanimation.saf_planner.dtos.label;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateLabelDescriptionRequest extends UpdateLabelRequest {
    @Size(max = 50, message = "Description is too long")
    private String description;
}
