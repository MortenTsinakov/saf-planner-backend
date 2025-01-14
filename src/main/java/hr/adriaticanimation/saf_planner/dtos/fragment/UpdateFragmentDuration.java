package hr.adriaticanimation.saf_planner.dtos.fragment;

import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class UpdateFragmentDuration extends UpdateFragmentRequest {
    @Min(value = 1, message = "Fragment's duration has to be at least 1 second")
    private Integer durationInSeconds;
}
