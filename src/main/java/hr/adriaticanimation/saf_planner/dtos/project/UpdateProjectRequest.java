package hr.adriaticanimation.saf_planner.dtos.project;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.Optional;

public record UpdateProjectRequest(
        @NotNull
        Optional<@NotBlank(message = "Project title cannot be blank")
                @Length(min = 1, max = 255, message = "Project title length has to be between 1 and 255 characters") String> title,
        @NotNull
        @Valid Optional<String> description,
        @NotNull
        @Valid Optional<@Min(value = 0) Integer> estimatedLengthInSeconds
) {}
