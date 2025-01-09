package hr.adriaticanimation.saf_planner.dtos.fragment;

import lombok.Getter;

@Getter
public class UpdateFragmentOnTimelineStatus extends UpdateFragmentRequest {
    private boolean onTimeline;
}
