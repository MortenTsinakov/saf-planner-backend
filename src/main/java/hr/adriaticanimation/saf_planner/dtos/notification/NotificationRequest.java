package hr.adriaticanimation.saf_planner.dtos.notification;

public record NotificationRequest(
        Long recipient,
        String summary,
        String message
) {}
