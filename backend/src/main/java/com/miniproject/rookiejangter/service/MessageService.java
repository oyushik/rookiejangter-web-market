package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.MessageDTO;
import com.miniproject.rookiejangter.entity.Chat;
import com.miniproject.rookiejangter.entity.Message;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.ChatRepository;
import com.miniproject.rookiejangter.repository.MessageRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    /**
     * 특정 채팅방에 메시지를 전송합니다.
     *
     * @param chatId 메시지를 전송할 채팅방 ID
     * @param request 메시지 내용 요청 DTO
     * @param senderId 메시지를 보내는 사용자 ID
     * @return 전송된 메시지의 응답 DTO
     */
    public MessageDTO.Response sendMessage(Long chatId, MessageDTO.Request request, Long senderId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHATROOM_NOT_FOUND, chatId));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, senderId));

        // 메시지 전송 시에는 아직 읽지 않은 상태 (isRead = false)
        Message message = Message.builder()
                .chat(chat)
                .sender(sender)
                .content(request.getContent())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);
        return MessageDTO.Response.fromEntity(savedMessage, chatId);
    }

    /**
     * 특정 채팅방의 메시지 목록을 조회합니다.
     *
     * @param chatId 메시지 목록을 조회할 채팅방 ID
     * @param pageable 페이징 및 정렬 정보
     * @return 채팅방의 메시지 목록 DTO
     */
    @Transactional(readOnly = true)
    public MessageDTO.MessageListResponse getMessagesByChatId(Long chatId, Pageable pageable) {
        // 채팅방 존재 여부 확인 (필요하다면)
        if (!chatRepository.existsById(chatId)) {
            throw new BusinessException(ErrorCode.CHATROOM_NOT_FOUND, chatId);
        }

        Page<Message> messagePage = messageRepository.findByChat_ChatId(chatId, pageable);

        List<MessageDTO.MessageListResponse.MessageResponse> messageResponses = messagePage.getContent().stream()
                .map(MessageDTO.MessageListResponse.MessageResponse::fromEntity)
                .collect(Collectors.toList());

        return MessageDTO.MessageListResponse.builder()
                .page(messagePage.getNumber())
                .size(messagePage.getSize())
                .totalElements(messagePage.getTotalElements())
                .totalPages(messagePage.getTotalPages())
                .first(messagePage.isFirst())
                .last(messagePage.isLast())
                .content(messageResponses)
                .build();
    }

    /**
     * 특정 메시지를 읽음 상태로 변경합니다.
     *
     * @param messageId 읽음 처리할 메시지 ID
     */
    public void markMessageAsRead(Long messageId) {
        // 메시지 존재 여부 확인
        if (!messageRepository.existsById(messageId)) {
            throw new BusinessException(ErrorCode.MESSAGE_NOT_FOUND, messageId);
        }
        messageRepository.updateIsReadByMessageId(true, messageId);
    }

    /**
     * 특정 채팅방 내에서 특정 사용자를 수신자로 하는 모든 읽지 않은 메시지를 읽음 상태로 변경합니다.
     * (즉, userId는 메시지를 읽는 사람의 ID이며, 해당 사용자가 받은 메시지 중 읽지 않은 메시지를 대상으로 함)
     *
     * @param chatId 메시지를 읽음 처리할 채팅방 ID
     * @param receiverId 메시지를 읽는 사용자 ID (로그인한 사용자)
     */
    public void markAllMessagesAsRead(Long chatId, Long receiverId) {
        // 채팅방 존재 여부 확인
        if (!chatRepository.existsById(chatId)) {
            throw new BusinessException(ErrorCode.CHATROOM_NOT_FOUND, chatId);
        }

        // 특정 채팅방에서 receiverId의 메시지 중 읽지 않은 메시지들을 찾아서 읽음 처리
        List<Message> unreadMessages = messageRepository.findByChat_ChatIdAndIsReadFalseAndReceiver_UserId(chatId, false, receiverId);

        unreadMessages.forEach(message -> messageRepository.updateIsReadByMessageId(true, message.getMessageId()));
    }
}