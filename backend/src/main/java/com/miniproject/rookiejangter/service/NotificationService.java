package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.controller.dto.NotificationDTO;
import com.miniproject.rookiejangter.entity.Notification;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.NotificationRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationDTO.Response createNotification(Long userId, Long entityId, String entityType, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        Notification notification = Notification.builder()
                .user(user)
                .entityId(entityId)
                .entityType(entityType)
                .message(message)
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return NotificationDTO.Response.fromEntity(savedNotification);
    }

    public List<NotificationDTO.Response> getUnreadNotifications() {
        List<Notification> notifications = notificationRepository.findByIsRead(false);

        return notifications.stream()
                .map(NotificationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }
}