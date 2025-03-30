package hr.adriaticanimation.saf_planner.dtos.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCommentRequest(
        @NotNull(message = "Missing comment id")
        Long commentId,
        @NotBlank(message = "Comment can't be blank")
        String content
) {}
