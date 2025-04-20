package hr.adriaticanimation.saf_planner.services.notification;

import hr.adriaticanimation.saf_planner.dtos.notification.MarkNotificationAsReadRequest;
import hr.adriaticanimation.saf_planner.dtos.notification.MarkNotificationAsReadResponse;
import hr.adriaticanimation.saf_planner.dtos.notification.MarkNotificationsAsReadResponse;
import hr.adriaticanimation.saf_planner.dtos.notification.NotificationRequest;
import hr.adriaticanimation.saf_planner.dtos.notification.NotificationResponse;
import hr.adriaticanimation.saf_planner.entities.comment.Comment;
import hr.adriaticanimation.saf_planner.entities.fragment.Fragment;
import hr.adriaticanimation.saf_planner.entities.notification.Notification;
import hr.adriaticanimation.saf_planner.entities.project.Project;
import hr.adriaticanimation.saf_planner.entities.user.User;
import hr.adriaticanimation.saf_planner.exceptions.custom_exceptions.ResourceNotFoundException;
import hr.adriaticanimation.saf_planner.mappers.notification.NotificationMapper;
import hr.adriaticanimation.saf_planner.repositories.notification.NotificationRepository;
import hr.adriaticanimation.saf_planner.repositories.user.UserRepository;
import hr.adriaticanimation.saf_planner.services.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final ConcurrentHashMap<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Subscribe to real time notifications.
     * @return - SSE-emitter that is used to send notifications to the client.
     */
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        User user = authenticationService.getUserFromSecurityContextHolder();

        emitters.put(user.getId(), emitter);

        emitter.onCompletion(() -> emitters.remove(user.getId()));
        emitter.onTimeout(() -> emitters.remove(user.getId()));
        emitter.onError((e) -> emitters.remove(user.getId()));

        return emitter;
    }

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

        emitNotification(notification);

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

    /**
     * Create a new notification about a shared project and emit the notification to the recipient.
     *
     * @param owner - owner of the shared project
     * @param sharedWith - the user who the project was shared with and who will receive the notification
     * @param project - project that was shared
     */
    public void createSharedProjectNotification(User owner, User sharedWith, Project project) {
        String summary = String.format("%s %s shared their project with you", owner.getFirstName(), owner.getLastName());
        String message = String.format("%s %s shared their project '%s' with you", owner.getFirstName(), owner.getLastName(), project.getTitle());
        Notification notification = Notification.builder()
                .recipient(sharedWith)
                .sender(owner)
                .summary(summary)
                .message(message)
                .isRead(false)
                .createdAt(Timestamp.from(Instant.now()))
                .build();
        notification = notificationRepository.save(notification);
        emitNotification(notification);
    }

    /**
     * Create a new notification when a fragment has been commented and emit the notification
     * to the owner of the project where the fragment belongs to.
     *
     * @param commenter - the user who wrote the comment
     * @param fragment - fragment that was commented
     * @param comment - comment that was written
     */
    public void createFragmentCommentNotification(User commenter, User owner, Fragment fragment, Comment comment) {
        String summary = String.format("%s %s commented your project", commenter.getFirstName(), commenter.getLastName());
        String message = String.format(
                """
                %s %s commented a fragment in your project '%s'.
                
                The fragment:
                %s
                
                The comment:
                %s
                """,
                commenter.getFirstName(),
                commenter.getLastName(),
                fragment.getProject().getTitle(),
                fragment.getLongDescription(),
                comment.getContent()
        );

        Notification notification = Notification.builder()
                .recipient(owner)
                .sender(commenter)
                .summary(summary)
                .message(message)
                .createdAt(Timestamp.from(Instant.now()))
                .isRead(false)
                .build();
        notification = notificationRepository.save(notification);
        emitNotification(notification);
    }

    /**
     * Send notification to the recipient if they have registered an emitter.
     * @param notification - notification to send.
     */
    private void emitNotification(Notification notification) {
        Long recipientId = notification.getRecipient().getId();
        SseEmitter emitter = emitters.get(recipientId);
        if (emitter != null) {
            NotificationResponse notificationResponse = notificationMapper.notificationToNotificationResponse(notification);
            try {
                emitter.send(notificationResponse);
            } catch (IOException e) {
                emitter.complete();
            }
        }
    }
}
