package hr.adriaticanimation.saf_planner.dtos.project;

import hr.adriaticanimation.saf_planner.utils.validators.OptionalInterval;
import hr.adriaticanimation.saf_planner.utils.validators.OptionalLength;
import hr.adriaticanimation.saf_planner.utils.validators.OptionalNotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record UpdateProjectRequest(
        @NotNull
        @OptionalNotBlank(message = "Project title can't be blank")
        @OptionalLength(min = 1, max = 255, message = "Project title length has to be between 1 and 255 characters")
        Optional<String> title,
        @NotNull
        Optional<String> description,
        @NotNull
        @OptionalInterval(min = 1, max = 10800, message = "Estimated length has to be >= 1 and <= 10800")
        Optional<Integer> estimatedLengthInSeconds
) {}
