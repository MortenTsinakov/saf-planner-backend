package hr.adriaticanimation.saf_planner.repositories.notification;

import hr.adriaticanimation.saf_planner.entities.notification.Notification;
import hr.adriaticanimation.saf_planner.entities.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByRecipientAndIsReadFalseOrderByCreatedAtDesc(User recipient);
    Optional<Notification> getNotificationById(Long id);
    @Modifying
    @Transactional
    @Query("UPDATE Notification e " +
           "SET e.isRead = true " +
           "WHERE e.recipient.id = :userId AND e.isRead = false")
    void markAllNotificationsAsRead(@Param("userId") Long userId);
}
