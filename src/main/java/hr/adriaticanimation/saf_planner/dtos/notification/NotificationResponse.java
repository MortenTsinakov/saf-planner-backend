package hr.adriaticanimation.saf_planner.dtos.notification;

import java.sql.Timestamp;

public record NotificationResponse(
        Long id,
        String summary,
        String message,
        Timestamp createdAt
) {}
