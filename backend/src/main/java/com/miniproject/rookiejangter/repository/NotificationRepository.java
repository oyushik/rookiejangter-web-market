package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Notification findByNotificationId(Long notificationId);
    List<Notification> findByUser_UserId(Long userUserId);
    List<Notification> findByEntityId(Long entityId);
    List<Notification> findByEntityType(String entityType);
    List<Notification> findByIsRead(Boolean isRead);
}