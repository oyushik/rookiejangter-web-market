package com.miniproject.rookiejangter.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "entity_id")
    private Long entityId;

    @Size(max = 20, message = "엔티티 타입은 최대 20자까지 가능합니다.")
    @Column(name = "entity_type", length = 20)
    private String entityType;

    @Size(max = 255, message = "메시지는 최대 255자까지 가능합니다.")
    @Column(name = "message", length = 255)
    private String message;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "is_read")
    private Boolean isRead;

    // 알림의 읽음 상태를 true로 업데이트
    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
        }
    }
}
