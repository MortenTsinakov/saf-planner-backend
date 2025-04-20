package hr.adriaticanimation.saf_planner.services.notification;

import hr.adriaticanimation.saf_planner.dtos.notification.MarkNotificationAsReadRequest;
import hr.adriaticanimation.saf_planner.dtos.notification.MarkNotificationAsReadResponse;
import hr.adriaticanimation.saf_planner.dtos.notification.MarkNotificationsAsReadResponse;
import hr.adriaticanimation.saf_planner.dtos.notification.NotificationRequest;
import hr.adriaticanimation.saf_planner.dtos.notification.NotificationResponse;
import hr.adriaticanimation.saf_planner.entities.notification.Notification;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ResourceNotFoundException;
import hr.adriaticanimation.saf_planner.mappers.notification.NotificationMapper;
import hr.adriaticanimation.saf_planner.repositories.notification.NotificationRepository;
import hr.adriaticanimation.saf_planner.repositories.user.UserRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    /**
     * Fetch all unread notifications directed at the user and return them.
     * @return - response containing the list of all unread notifications.
     */
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications() {
        User user = authenticationService.getUserFromSecurityContextHolder();
        List<Notification> notifications = notificationRepository.findAllByRecipientAndIsReadFalseOrderByCreatedAtDesc(user);
        List<NotificationResponse> response = notificationMapper.notificationsToNotificationResponses(notifications);
        return ResponseEntity.ok(response);
    }

    /**
     * Test endpoint for adding notifications
     * @param request - notification request
     * @return - the added notification
     */
    public ResponseEntity<NotificationResponse> addTestNotification(NotificationRequest request) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        User recipient = userRepository.findById(request.recipient())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

        Notification notification = Notification.builder()
                .recipient(recipient)
                .sender(user)
                .summary(request.summary())
                .message(request.message())
                .createdAt(Timestamp.from(Instant.now()))
                .isRead(false)
                .build();
        notification = notificationRepository.save(notification);
        NotificationResponse response = notificationMapper.notificationToNotificationResponse(notification);

        return ResponseEntity.ok(response);
    }

    /**
     * Set the is_read field on a specific notification to true.
     * @param request - contains notification id
     * @return - Response containing a success message
     */
    public ResponseEntity<MarkNotificationAsReadResponse> markNotificationAsRead(MarkNotificationAsReadRequest request) {
        User user = authenticationService.getUserFromSecurityContextHolder();
        Notification notification = notificationRepository.getNotificationById(request.id())
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Notification not found");
        }

        notification.setRead(true);
        notificationRepository.save(notification);

        MarkNotificationAsReadResponse response = new MarkNotificationAsReadResponse(notification.getId(), "Notification marked as read");
        return ResponseEntity.ok(response);
    }

    /**
     * Mark all user's notifications as read
     * @return - response containing a success message
     */
    public ResponseEntity<MarkNotificationsAsReadResponse> markAllNotificationsAsRead() {
        User user = authenticationService.getUserFromSecurityContextHolder();
        notificationRepository.markAllNotificationsAsRead(user.getId());
        MarkNotificationsAsReadResponse response = new MarkNotificationsAsReadResponse("Notifications marked as read");
        return ResponseEntity.ok(response);
    }
}
