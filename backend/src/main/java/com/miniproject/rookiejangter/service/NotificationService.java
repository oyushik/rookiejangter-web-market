package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.NotificationDTO;
import com.miniproject.rookiejangter.entity.Notification;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.NotificationRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /** 알림 생성
     * 특정 사용자에게 알림을 생성합니다.
     *
     * @param userId      사용자 ID
     * @param entityId    엔티티 ID (예: 상품 ID)
     * @param entityType  엔티티 타입 (예: "PRODUCT")
     * @param message     알림 메시지
     * @return 생성된 알림 정보
     */
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

    /** 특정 알림을 ID로 조회합니다.
     *
     * @param notificationId 알림 ID
     * @return 조회된 알림 정보
     */
    public NotificationDTO.Response getNotificationById(Long notificationId) {
        Notification notification = notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Notification", notificationId));

        return NotificationDTO.Response.fromEntity(notification);
    }

    /** 특정 사용자의 알림을 페이지네이션하여 조회합니다.
     *
     * @param userId   사용자 ID
     * @param pageable 페이지 정보
     * @return 페이지네이션된 알림 목록
     */
    public Page<NotificationDTO.Response> getNotificationsByUserId(Long userId, Pageable pageable) {
        Page<Notification> notificationsPage = notificationRepository.findByUser_UserId(userId, pageable);
        return notificationsPage.map(NotificationDTO.Response::fromEntity);
    }

    /** 특정 사용자의 읽지 않은 알림 개수를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 읽지 않은 알림 개수
     */
    public long countUnreadNotificationsByUserId(Long userId) {
        return notificationRepository.countByUser_UserIdAndIsRead(userId, false);
    }
    
    
    /** 특정 엔티티 ID와 관련된 알림 조회
     *
     * @param entityId 엔티티 ID (예: 상품 ID)
     * @return 해당 엔티티와 관련된 알림 목록
     */
    public List<NotificationDTO.Response> getNotificationsByEntityId(Long entityId) {
        List<Notification> notifications = notificationRepository.findByEntityId(entityId);

        return notifications.stream()
                .map(NotificationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /** 특정 엔티티 타입과 관련된 알림 조회
     *
     * @param entityType 엔티티 타입 (예: "PRODUCT")
     * @return 해당 엔티티 타입과 관련된 알림 목록
     */
    public List<NotificationDTO.Response> getNotificationsByEntityType(String entityType) {
        List<Notification> notifications = notificationRepository.findByEntityType(entityType);

        return notifications.stream()
                .map(NotificationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }
    
    /** 읽지 않은 알림 조회
     *
     * @return 읽지 않은 알림 목록
     */
    public List<NotificationDTO.Response> getUnreadNotifications() {
        List<Notification> notifications = notificationRepository.findByIsRead(false);

        return notifications.stream()
                .map(NotificationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /** 특정 알림을 읽음 처리합니다.
     *
     * @param notificationId 알림 ID
     */
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Notification", notificationId));
        
        notification.markAsRead();
    }

    /** 특정 알림을 삭제합니다.
     *
     * @param notificationId 알림 ID
     */
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationRepository.findByNotificationId(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Notification", notificationId));

        notificationRepository.delete(notification);
    }
}