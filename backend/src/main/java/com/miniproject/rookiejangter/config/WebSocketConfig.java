package com.miniproject.rookiejangter.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // WebSocket 메시지 브로커를 활성화
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthenticationInterceptor webSocketAuthenticationInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지를 구독하는 요청의 prefix
        // "/sub"로 시작하는 메시지는 메시지 브로커가 처리
        registry.enableSimpleBroker("/sub");
        
        // 메시지를 발행하는 요청의 prefix
        // "/pub"로 시작하는 메시지는 @MessageMapping 메서드로 라우팅
        registry.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket Handshake를 위한 엔드포인트 설정
        // 클라이언트는 "/ws/chat"으로 WebSocket 연결을 맺음
        // SockJS는 WebSocket을 지원하지 않는 브라우저를 위해 폴백 옵션을 제공
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("http://localhost:3000") // CORS 허용 도메인
                .withSockJS(); // SockJS 지원 활성화 (브라우저 호환성 위함)
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 클라이언트로부터 들어오는 STOMP 메시지에 대한 인터셉터 등록
        // CONNECT 메시지에서 JWT 토큰을 처리하여 인증을 수행합니다.
        registration.interceptors(webSocketAuthenticationInterceptor);
    }
}