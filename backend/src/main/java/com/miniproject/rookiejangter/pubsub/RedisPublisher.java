package com.miniproject.rookiejangter.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniproject.rookiejangter.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisPublisher {

    @Qualifier("chatRedisTemplate") // "chatRedisTemplate" 빈 주입
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Redis Topic에 메시지 발행
     *
     * @param topicName 발행할 토픽 (채널) 이름
     * @param message 발행할 메시지 객체
     */
    public void publish(String topicName, MessageDTO.Response message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(topicName, jsonMessage);
            log.info("Redis published message to topic {}: {}", topicName, jsonMessage);
        } catch (Exception e) {
            log.error("Failed to publish message to Redis topic {}: {}", topicName, e.getMessage());
        }
    }
}