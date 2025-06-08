package service;

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

    public MessageDTO.Response sendMessage(Long chatRoomId, MessageDTO.Request request, Long senderId) {
        Chat chat = chatRepository.findById(chatRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHATROOM_NOT_FOUND, chatRoomId));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, senderId));

        Message message = Message.builder()
                .chat(chat)
                .user(sender)
                .content(request.getContent())
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();

        Message savedMessage = messageRepository.save(message);
        return MessageDTO.Response.fromEntity(savedMessage, chatRoomId);
    }

    public MessageDTO.MessageListResponse getMessagesByChatId(Long chatRoomId) {
        List<Message> messages = messageRepository.findByChat_ChatId(chatRoomId);

        List<MessageDTO.MessageListResponse.MessageResponse> messageResponses = messages.stream()
                .map(MessageDTO.MessageListResponse.MessageResponse::fromEntity)
                .collect(Collectors.toList());

        //TODO: Pageable 처리 필요
        return MessageDTO.MessageListResponse.builder()
                .page(0)
                .size(messageResponses.size())
                .totalElements(messageResponses.size())
                .totalPages(1)
                .first(true)
                .last(true)
                .content(messageResponses)
                .build();
    }

    public void markMessageAsRead(Long messageId) {
        messageRepository.updateIsReadByMessageId(true, messageId);
    }

    public void markAllMessagesAsRead(Long chatRoomId, Long userId) {
        List<Message> messages = messageRepository.findByChat_ChatId(chatRoomId);
        messages.stream()
                .filter(message -> message.getUser().getUserId().equals(userId))
                .forEach(message -> messageRepository.updateIsReadByMessageId(true, message.getMessageId()));
    }
}