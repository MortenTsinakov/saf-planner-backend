package hr.adriaticanimation.saf_planner.controllers.notification;

import hr.adriaticanimation.saf_planner.dtos.notification.MarkNotificationAsReadRequest;
import hr.adriaticanimation.saf_planner.dtos.notification.MarkNotificationAsReadResponse;
import hr.adriaticanimation.saf_planner.dtos.notification.MarkNotificationsAsReadResponse;
import hr.adriaticanimation.saf_planner.dtos.notification.NotificationRequest;
import hr.adriaticanimation.saf_planner.dtos.notification.NotificationResponse;
import hr.adriaticanimation.saf_planner.services.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(description = "Subscribe to real time notifications")
    public SseEmitter subscribe() {
        return notificationService.subscribe();
    }

    @GetMapping
    @Operation(description = "Fetch all unread notifications for the user")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications() {
        return notificationService.getUnreadNotifications();
    }

    @PostMapping
    @Operation(description = "Test endpoint for adding notifications")
    public ResponseEntity<NotificationResponse> addTestNotification(@RequestBody NotificationRequest request) {
        return notificationService.addTestNotification(request);
    }

    @PostMapping("/mark-all-as-read")
    @Operation(description = "Mark all user's notifications as read")
    public ResponseEntity<MarkNotificationsAsReadResponse> markAllAsRead() {
        return notificationService.markAllNotificationsAsRead();
    }

    @PatchMapping
    @Operation(description = "Mark notification as read")
    public ResponseEntity<MarkNotificationAsReadResponse> markNotificationAsRead(@RequestBody MarkNotificationAsReadRequest request) {
        return notificationService.markNotificationAsRead(request);
    }
}
