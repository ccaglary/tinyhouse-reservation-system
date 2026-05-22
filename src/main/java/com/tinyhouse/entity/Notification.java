package com.tinyhouse.entity;
import com.tinyhouse.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "notifications") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(name = "title", nullable = false, length = 200) private String title;
    @Column(name = "message", nullable = false, columnDefinition = "TEXT") private String message;
    @Enumerated(EnumType.STRING) @Column(name = "notification_type", nullable = false, length = 30) private NotificationType notificationType;
    @Column(name = "is_read", nullable = false) @Builder.Default private boolean isRead = false;
}
