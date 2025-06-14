package com.miniproject.rookiejangter.config;

import com.miniproject.rookiejangter.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketAuthenticationInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider; // <-- 여기도 수정!

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            System.out.println("WebSocketAuthenticationInterceptor: CONNECT command detected.");

            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                System.out.println("WebSocketAuthenticationInterceptor: Authorization header found. Token: " + token);

                try {
                    if (jwtProvider.validateToken(token)) { // jwtProvider 사용
                        Authentication authentication = jwtProvider.getAuthentication(token); // jwtProvider 사용
                        if (authentication != null && authentication.isAuthenticated()) {
                            accessor.setUser(authentication);
                            System.out.println("WebSocketAuthenticationInterceptor: User authenticated: " + authentication.getName());
                        } else {
                            System.err.println("WebSocketAuthenticationInterceptor: Authentication object is null or not authenticated after getAuthentication for token: " + token);
                        }
                    } else {
                        System.err.println("WebSocketAuthenticationInterceptor: JWT token validation failed for token: " + token);
                    }
                } catch (Exception e) {
                    System.err.println("WebSocketAuthenticationInterceptor: Exception during JWT token processing: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("WebSocketAuthenticationInterceptor: No valid Authorization header (Bearer token) found for WebSocket connection.");
            }
        }
        return message;
    }
}