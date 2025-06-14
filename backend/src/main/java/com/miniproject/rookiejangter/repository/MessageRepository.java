package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByChat_ChatId(Long chatChatId, Pageable pageable);
    List<Message> findByChat_ChatIdOrderByCreatedAtDesc(Long chatChatId);

    List<Message> findByChat_ChatIdAndIsReadFalseAndSender_UserIdNot(Long chatChatId, Long senderUserId);
    
    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.isRead = :isRead WHERE m.messageId = :messageId")
    void updateIsReadByMessageId(Boolean isRead, Long messageId);
}