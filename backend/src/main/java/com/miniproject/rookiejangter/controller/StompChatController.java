package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.dto.MessageDTO;
import com.miniproject.rookiejangter.pubsub.RedisPublisher;
import com.miniproject.rookiejangter.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@Controller
// @CrossOrigin은 WebSocket 연결에는 직접적으로 영향을 주지 않지만,
// 웹 애플리케이션의 다른 부분에서 CORS 문제가 발생할 경우 유용합니다.
// WebSocket 자체는 Handshake 시 HTTP를 사용하므로 CORS 정책의 영향을 받습니다.
@CrossOrigin(origins = "http://localhost:3000")
public class StompChatController {

    private final RedisPublisher redisPublisher;
    private final MessageService messageService; // DB에 메시지 저장 로직을 위해 사용

    /**
     * 클라이언트로부터 STOMP 메시지를 수신합니다.
     * "/pub/chat/message/{chatRoomId}"로 메시지가 발행되면 이 메서드가 호출됩니다.
     *
     * @param chatRoomId 메시지를 보낼 채팅방 ID
     * @param request 메시지 내용 요청 DTO
     * @param headerAccessor STOMP 메시지 헤더 접근자 (여기서 Principal을 가져올 수 있음)
     */
    @MessageMapping("/chat/message/{chatRoomId}") // 실제 경로는 "/pub/chat/message/{chatRoomId}"
    public void message(
            @DestinationVariable Long chatRoomId,
            MessageDTO.Request request,
            SimpMessageHeaderAccessor headerAccessor) {
        try {
            // 현재 로그인한 사용자 ID 가져오기
            // WebSocketHandshakeInterceptor 등에서 Principal을 설정해야 합니다.
            // 여기서는 임시로 Principal에서 가져오지만, 실제 환경에서는 JWT를 파싱하여 사용자 정보를 가져오는 로직이 필요합니다.
            // Spring Security WebSocket 설정이 필요합니다.
            Principal principal = headerAccessor.getUser();
            if (principal == null) {
                log.error("WebSocket message received without authenticated user.");
                // 인증되지 않은 사용자 처리 로직 (예: 에러 메시지 전송 또는 연결 종료)
                return;
            }
            Long senderId = Long.parseLong(principal.getName()); // JWT 토큰의 subject에서 userId 추출

            log.info("Received WebSocket message for chatRoomId {}: {}", chatRoomId, request.getContent());

            // 1. DB에 메시지 저장
            MessageDTO.Response savedMessage = messageService.sendMessage(chatRoomId, request, senderId);
            log.info("Message saved to DB: {}", savedMessage);

            // 2. Redis Pub/Sub을 통해 메시지 발행
            // 이 메시지는 RedisSubscriber에 의해 수신되어 해당 채팅방 구독자들에게 다시 WebSocket으로 전송됩니다.
            redisPublisher.publish("chatRoom." + chatRoomId, savedMessage);
            log.info("Message published to Redis topic chatRoom.{}", chatRoomId);

        } catch (Exception e) {
            log.error("Error processing WebSocket message for chatRoomId {}: {}", chatRoomId, e.getMessage(), e);
            // 에러 발생 시 클라이언트에게 에러 메시지를 보낼 수 있음
            // messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/errors", "메시지 전송 실패");
        }
    }
}