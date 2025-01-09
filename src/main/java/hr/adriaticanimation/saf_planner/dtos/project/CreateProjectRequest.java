package hr.adriaticanimation.saf_planner.dtos.project;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CreateProjectRequest(
        @NotBlank(message = "Project has to have a title")
        @Length(min = 1, max = 255, message = "The length of the title has to be between 1 and 255 characters")
        String title,
        String description,
        @Min(value = 0, message = "Estimated length cannot be a negative value")
        Integer estimatedLengthInSeconds
) {}
