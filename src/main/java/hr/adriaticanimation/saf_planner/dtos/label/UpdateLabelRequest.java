package hr.adriaticanimation.saf_planner.dtos.label;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateLabelRequest {
    @NotNull
    private Long labelId;
}