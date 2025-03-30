package hr.adriaticanimation.saf_planner.dtos.comment;

import java.sql.Timestamp;

public record CommentResponse(
    Long id,
    String content,
    String author,
    Timestamp lastUpdated
) {}
