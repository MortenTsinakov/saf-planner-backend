package hr.adriaticanimation.saf_planner.mappers.notification;

import hr.adriaticanimation.saf_planner.dtos.notification.NotificationResponse;
import hr.adriaticanimation.saf_planner.entities.notification.Notification;
import org.mapstruct.Mapper;

import java.sql.Timestamp;
import java.util.List;

@Mapper(componentModel = "spring", imports = {Timestamp.class})
public interface NotificationMapper {
    NotificationResponse notificationToNotificationResponse(Notification notification);
    List<NotificationResponse> notificationsToNotificationResponses(List<Notification> notifications);
}
