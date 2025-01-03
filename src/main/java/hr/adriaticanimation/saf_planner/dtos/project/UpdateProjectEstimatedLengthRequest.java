package hr.adriaticanimation.saf_planner.dtos.project;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateProjectEstimatedLengthRequest(
   @NotNull
   Long projectId,
   @Min(value = 0, message = "Estimated length cannot be a negative value")
   Integer estimatedLengthInSeconds
) {}
