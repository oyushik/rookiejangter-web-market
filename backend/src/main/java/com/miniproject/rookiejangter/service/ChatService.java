package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.ChatDTO;
import com.miniproject.rookiejangter.entity.Chat;
import com.miniproject.rookiejangter.entity.Message;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.ChatRepository;
import com.miniproject.rookiejangter.repository.MessageRepository;
import com.miniproject.rookiejangter.repository.ProductRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    /**
     * 새로운 채팅방을 생성합니다.
     *
     * @param request 채팅방 생성 요청 DTO (판매자 ID, 상품 ID 포함)
     * @param buyerId 채팅방을 생성하는 구매자 ID
     * @return 생성된 채팅방의 응답 DTO
     */
    public ChatDTO.Response createChat(ChatDTO.Request request, Long buyerId) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, request.getProductId()));

        // 구매자 정보 조회 (현재 로그인한 사용자)
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, buyerId));

        // 판매자 정보 조회 (상품 등록자)
        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, request.getSellerId()));

        // 구매자가 판매자 자신인 경우 채팅방 생성 방지
        if (buyer.getUserId().equals(seller.getUserId())) {
            throw new BusinessException(ErrorCode.CANNOT_CHAT_WITH_SELF);
        }

        // 이미 존재하는 채팅방이 있는지 확인 (구매자, 판매자, 상품 기준으로)
        // TODO: 정확한 중복 채팅방 확인 로직 필요 (예: 구매자와 판매자 간 특정 상품에 대한 채팅방은 하나만 존재)
        // 현재는 단순히 없으면 생성하는 것으로 간주

        Chat chat = Chat.builder()
                .product(product)
                .buyer(buyer)
                .seller(seller)
                .build();

        Chat savedChat = chatRepository.save(chat);
        return ChatDTO.Response.fromEntity(savedChat);
    }

    /**
     * 특정 ID의 채팅방 정보를 조회합니다.
     *
     * @param chatId 조회할 채팅방 ID
     * @return 조회된 채팅방의 응답 DTO
     */
    @Transactional(readOnly = true)
    public ChatDTO.Response getChatById(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHATROOM_NOT_FOUND, chatId));
        return ChatDTO.Response.fromEntity(chat);
    }

    /**
     * 특정 사용자가 참여하고 있는 모든 채팅방 목록을 조회합니다.
     * 각 채팅방의 마지막 메시지를 포함합니다.
     *
     * @param userId 채팅방 목록을 조회할 사용자 ID
     * @param pageable 페이징 및 정렬 정보
     * @return 사용자가 참여하고 있는 채팅방 목록 DTO
     */
    @Transactional(readOnly = true)
    public ChatDTO.ChatListResponse getChatsByUserId(Long userId, Pageable pageable) {
        // 구매자로서 참여한 채팅방과 판매자로서 참여한 채팅방을 모두 조회
        Page<Chat> chatPage = chatRepository.findByBuyer_UserIdOrSeller_UserId(userId, userId, pageable);

        List<ChatDTO.ChatListResponse.ChatInfo> chatInfoList = chatPage.getContent().stream()
                .map(chat -> {
                    // 각 채팅방의 마지막 메시지 조회
                    List<Message> messages = messageRepository.findByChat_ChatIdOrderByCreatedAtDesc(chat.getChatId());
                    String lastMessageContent = messages.isEmpty() ? "메시지가 없습니다." : messages.get(0).getContent();
                    return ChatDTO.ChatListResponse.ChatInfo.fromEntity(chat,lastMessageContent);
                })
                .collect(Collectors.toList());

        return ChatDTO.ChatListResponse.builder()
                .page(chatPage.getNumber())
                .size(chatPage.getSize())
                .totalElements(chatPage.getTotalElements())
                .totalPages(chatPage.getTotalPages())
                .first(chatPage.isFirst())
                .last(chatPage.isLast())
                .content(chatInfoList)
                .build();
    }

    /**
     * 특정 채팅방을 삭제합니다.
     *
     * @param chatId 삭제할 채팅방 ID
     */
    public void deleteChat(Long chatId) {
        // 채팅방 존재 여부 확인
        if (!chatRepository.existsById(chatId)) {
            throw new BusinessException(ErrorCode.CHATROOM_NOT_FOUND, chatId);
        }
        chatRepository.deleteById(chatId);
    }
}