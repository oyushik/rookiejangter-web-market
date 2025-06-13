package com.miniproject.rookiejangter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.miniproject.rookiejangter.pubsub.RedisSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    /**
     * 채팅 Pub/Sub 및 객체 저장을 위한 RedisTemplate
     * 빈 이름을 'chatRedisTemplate'으로 명시하여 기존 'redisTemplate'과 충돌 방지
     */
    @Bean(name = "chatRedisTemplate") // 빈 이름 명시
    public RedisTemplate<String, Object> chatRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // Key 직렬화
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // Value 직렬화 (JSON 형식으로 저장) - Deprecated된 setObjectMapper 대신 생성자 사용
        // RedisConfig에 별도로 등록된 objectMapper 빈을 사용하여 Jackson2JsonRedisSerializer를 초기화
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper(), Object.class); // 수정된 부분: 생성자에서 objectMapper 전달

        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    /**
     * Redis Connection Factory
     * 스프링 부트에서 자동으로 설정해주므로 주입받아 사용
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory, RedisSubscriber redisSubscriber) { // MessageListenerAdapter는 직접 빈으로 등록
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // RedisSubscriber를 감싸는 MessageListenerAdapter를 등록합니다.
        // 이 컨테이너는 Redis로부터 "chatRoom.*" 패턴의 메시지를 수신하면 redisSubscriber의 onMessage 메서드를 호출합니다.
        container.addMessageListener(messageListenerAdapter(redisSubscriber), new ChannelTopic("chatRoom.*"));

        return container;
    }

    /**
     * Redis Pub/Sub 메시지 수신 시 RedisSubscriber의 onMessage 메서드를 호출하기 위한 어댑터
     */
    @Bean
    MessageListenerAdapter messageListenerAdapter(RedisSubscriber redisSubscriber) {
        // RedisSubscriber의 onMessage 메서드가 Redis 메시지를 처리하도록 설정
        return new MessageListenerAdapter(redisSubscriber);
    }

    /**
     * Redis Pub/Sub 메시지 발행/수신 시 JSON 처리를 위한 ObjectMapper.
     * RedisTemplate의 Jackson2JsonRedisSerializer 내부에서 사용됩니다.
     * RedisSubscriber에서도 이 ObjectMapper를 직접 주입받아 사용할 수 있습니다.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 타입 지원
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 날짜를 ISO 8601 형식으로
        // 필요한 경우 추가적인 Jackson 설정 (예: UNDEFINED_PROPERTIES_AS_EMPTY_OBJECTS)
        return objectMapper;
    }
}
