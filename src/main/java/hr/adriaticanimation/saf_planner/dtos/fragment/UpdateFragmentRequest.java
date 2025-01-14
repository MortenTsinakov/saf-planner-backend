package hr.adriaticanimation.saf_planner.dtos.fragment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateFragmentRequest {
    @NotNull
    private Long fragmentId;
}