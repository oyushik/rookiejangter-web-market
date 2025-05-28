package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Message findByMessageId(Long messageId);
    List<Message> findByChat_ChatId(Long chatChatId);
    List<Message> findByIsRead(Boolean isRead);
}