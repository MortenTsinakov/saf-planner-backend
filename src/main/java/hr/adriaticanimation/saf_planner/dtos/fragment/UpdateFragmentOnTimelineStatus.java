package hr.adriaticanimation.saf_planner.dtos.fragment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateFragmentOnTimelineStatus extends UpdateFragmentRequest {
    @NotNull
    private boolean onTimeline;
}
