package hr.adriaticanimation.saf_planner.entities.notification;

import hr.adriaticanimation.saf_planner.entities.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "notifications")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "recipient", referencedColumnName = "id", nullable = false)
    private User recipient;
    @ManyToOne
    @JoinColumn(name = "sender", referencedColumnName = "id", nullable = false)
    private User sender;
    @Column(name = "summary", nullable = false)
    private String summary;
    @Column(name = "message", nullable = false)
    private String message;
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
    @Column(name = "is_read", nullable = false)
    private boolean isRead;
}
