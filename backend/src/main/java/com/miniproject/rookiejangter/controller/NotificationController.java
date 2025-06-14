package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.dto.NotificationDTO;
import com.miniproject.rookiejangter.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 로그인 중인 사용자 알림 조회. token으로 조회
    @GetMapping
    public ResponseEntity<Page<NotificationDTO.Response>> getUserNotifications(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Long userId = Long.parseLong(authentication.getName());
        Page<NotificationDTO.Response> notifications = notificationService.getNotificationsByUserId(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    // 알림 읽음 표시
    @PatchMapping("/{notificationId}/read")
    @CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.PATCH, RequestMethod.OPTIONS})
    public ResponseEntity<Void> markNotificationAsRead(
            Authentication authentication,
            @PathVariable Long notificationId) {
        Long userId = Long.parseLong(authentication.getName());

        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    // 알림 삭제
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            Authentication authentication,
            @PathVariable Long notificationId) {
        Long userId = Long.parseLong(authentication.getName());

        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

    // 읽지 않은 알림 카운트
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadNotificationsCount(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        long count = notificationService.countUnreadNotificationsByUserId(userId);
        return ResponseEntity.ok(count);
    }
}