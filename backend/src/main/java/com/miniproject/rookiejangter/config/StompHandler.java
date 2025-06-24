package com.miniproject.rookiejangter.config;

import com.miniproject.rookiejangter.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // CONNECT 또는 SEND 명령일 때 JWT 토큰을 검증
        if (StompCommand.CONNECT == accessor.getCommand() || StompCommand.SEND == accessor.getCommand()) {
            // "Authorization" 헤더에서 JWT 토큰 추출
            String accessToken = accessor.getFirstNativeHeader("Authorization");

            if (accessToken != null && accessToken.startsWith("Bearer ")) {
                accessToken = accessToken.substring(7); // "Bearer " 제거

                // 토큰 유효성 검사 및 사용자 인증
                if (jwtProvider.validateToken(accessToken)) {
                    try {
                        // JwtTokenProvider를 통해 Authentication 객체 생성
                        Authentication authentication = jwtProvider.getAuthentication(accessToken);
                        accessor.setUser(authentication); // STOMP 세션에 사용자 정보 저장
                        log.info("WebSocket connection authenticated for user: {}", authentication.getName());
                    } catch (RuntimeException e) {
                        log.error("Failed to authenticate WebSocket connection: {}", e.getMessage());
                        // 인증 실패 시 연결 거부 (예외 발생)
                        throw e; // 예외를 던져 연결을 끊거나 에러 응답
                    }
                } else {
                    log.warn("Invalid JWT token for WebSocket connection.");
                    throw new RuntimeException("Invalid JWT token for WebSocket connection.");
                }
            } else if (StompCommand.SEND == accessor.getCommand() && accessor.getUser() == null) {
                // SEND 명령인데 인증 정보가 없으면 (CONNECT에서 인증 실패했거나 누락된 경우)
                log.warn("Attempt to send message without authenticated user in session.");
                throw new RuntimeException("Unauthenticated WebSocket message. Please connect with a valid token.");
            }
        }
        return message;
    }
}