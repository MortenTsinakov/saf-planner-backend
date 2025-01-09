package hr.adriaticanimation.saf_planner.dtos.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UpdateProjectTitleRequest(
        @NotNull
        Long projectId,
        @NotBlank(message = "Project title cannot be blank")
        @Length(min = 1, max = 255, message = "The length of the title has to be between 1 and 255 characters")
        String title
) {
}
