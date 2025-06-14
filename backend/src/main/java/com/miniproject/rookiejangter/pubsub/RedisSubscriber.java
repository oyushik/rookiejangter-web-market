package com.miniproject.rookiejangter.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniproject.rookiejangter.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate; // WebSocket 메시지를 클라이언트에게 전송

    /**
     * Redis에서 메시지가 발행되면 리스너가 이 메서드를 통해 메시지를 수신합니다.
     *
     * @param message 수신된 Redis 메시지
     * @param pattern 구독 패턴 (여기서는 사용하지 않음)
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Redis에서 수신된 메시지를 JSON 문자열로 변환
            String publishMessage = (String) new StringRedisSerializer().deserialize(message.getBody());
            
            // JSON 문자열을 MessageDTO.Response 객체로 역직렬화
            MessageDTO.Response chatMessage = objectMapper.readValue(publishMessage, MessageDTO.Response.class);
            log.info("Redis received message: {}", chatMessage);

            // WebSocket 구독자에게 메시지 전송
            // "/sub/chat/room/{chatId}" topic으로 메시지를 발행
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getChatId(), chatMessage);
            log.info("Sent WebSocket message to /sub/chat/room/{}: {}", chatMessage.getChatId(), chatMessage);

        } catch (Exception e) {
            log.error("Error processing Redis message: {}", e.getMessage(), e);
        }
    }
}