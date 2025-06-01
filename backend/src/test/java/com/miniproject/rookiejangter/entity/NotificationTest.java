package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 임베디드 DB 사용 시 제거 또는 replace = Replace.ANY 로 변경
public class NotificationTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createNotification() {
        // given
        User user = User.builder()
                .loginId("testuser")
                .password("password")
                .userName("테스트유저")
                .phone("010-1234-5678")
                .build();
        entityManager.persist(user);

        Notification notification = Notification.builder()
                .user(user)
                .entityId(1L)
                .entityType("product")
                .message("테스트 알림 메시지")
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();

        // when
        entityManager.persist(notification);
        entityManager.flush(); // 영속성 컨텍스트 변경 내용을 DB에 반영
        entityManager.clear(); // 영속성 컨텍스트 초기화
        Notification savedNotification = entityManager.find(Notification.class, notification.getNotificationId());

        // then
        assertThat(savedNotification).isNotNull();
        assertThat(savedNotification.getNotificationId()).isNotNull();
        assertThat(savedNotification.getUser()).isEqualTo(user);
        assertThat(savedNotification.getEntityId()).isEqualTo(1L);
        assertThat(savedNotification.getEntityType()).isEqualTo("product");
        assertThat(savedNotification.getMessage()).isEqualTo("테스트 알림 메시지");
        assertThat(savedNotification.getIsRead()).isFalse();
    }

    @Test
    void checkUserNotificationAssociation() {
        // given
        User user = User.builder()
                .loginId("userwithnoti")
                .password("pass")
                .userName("알림유저")
                .phone("010-9876-5432")
                .build();
        entityManager.persist(user);

        Notification notification1 = Notification.builder()
                .user(user)
                .entityId(10L)
                .entityType("chat")
                .message("첫 번째 알림")
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();
        entityManager.persist(notification1);

        Notification notification2 = Notification.builder()
                .user(user)
                .entityId(20L)
                .entityType("review")
                .message("두 번째 알림")
                .sentAt(LocalDateTime.now().plusMinutes(1))
                .isRead(true)
                .build();
        entityManager.persist(notification2);

        entityManager.flush();
        entityManager.clear();

        // when
        Notification foundNotification1 = entityManager.find(Notification.class, notification1.getNotificationId());
        Notification foundNotification2 = entityManager.find(Notification.class, notification2.getNotificationId());

        // then
        assertThat(foundNotification1).isNotNull();
        assertThat(foundNotification1.getUser()).isEqualTo(user);
        assertThat(foundNotification2).isNotNull();
        assertThat(foundNotification2.getUser()).isEqualTo(user);
        assertThat(user.getNotifications()).isNull(); // OneToMany 관계가 아니므로 User 엔티티에서 Notifications를 가지고 있지 않습니다.
    }
}