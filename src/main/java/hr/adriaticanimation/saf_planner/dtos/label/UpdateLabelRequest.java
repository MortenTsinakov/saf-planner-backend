package hr.adriaticanimation.saf_planner.dtos.label;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateLabelRequest {
    @NotNull
    private Long labelId;
}