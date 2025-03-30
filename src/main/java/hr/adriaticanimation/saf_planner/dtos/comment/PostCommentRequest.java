package hr.adriaticanimation.saf_planner.dtos.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PostCommentRequest(
        @NotNull(message = "Missing fragment id")
        Long fragmentId,
        @NotBlank(message = "Comment can't be blank")
        String content
) {}
