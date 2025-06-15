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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    // ... createChat (기존과 동일) ...
    public ChatDTO.Response createChat(ChatDTO.Request request) {
        // ... (기존 로직 유지) ...
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long buyerId = Long.parseLong(authentication.getName()); // 구매자 ID는 현재 로그인 유저
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, buyerId));

        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, request.getSellerId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, request.getProductId()));

        // 이미 존재하는 채팅방인지 확인 (구매자와 판매자, 상품 기준으로)
        Optional<Chat> existingChat = chatRepository.findByBuyerAndSellerAndProduct(buyer, seller, product);
        if (existingChat.isPresent()) {
            // 이미 존재하면 해당 채팅방 반환 (에러가 아니라 재사용)
            return ChatDTO.Response.fromEntity(existingChat.get());
        }

        Chat chat = Chat.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .build();

        Chat savedChat = chatRepository.save(chat);
        return ChatDTO.Response.fromEntity(savedChat);
    }


    /**
     * 특정 ID의 채팅방 정보를 조회합니다.
     * 채팅방 참여자만 조회할 수 있습니다.
     *
     * @param chatId 조회할 채팅방 ID
     * @param currentUserId 현재 로그인한 사용자 ID (컨트롤러에서 주입받음)
     * @return 조회된 채팅방 정보
     */
    public ChatDTO.Response getChatById(Long chatId, Long currentUserId) { // currentUserId 파라미터 추가
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHATROOM_NOT_FOUND, chatId));

        // **인가 로직을 서비스 메서드 내부로 이동**
        if (!chat.getBuyer().getUserId().equals(currentUserId) && !chat.getSeller().getUserId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.CHAT_FORBIDDEN_ACCESS);
        }

        return ChatDTO.Response.fromEntity(chat);
    }

    /**
     * 현재 로그인한 사용자가 참여하고 있는 모든 채팅방 목록을 조회합니다.
     *
     * @param pageable 페이징 및 정렬 정보
     * @return 사용자가 참여하고 있는 채팅방 목록
     */
    public ChatDTO.ChatListResponse getChatsByUserId(Pageable pageable) {
        // 이 메서드에서는 컨트롤러에서 Principal을 받지 않고 SecurityContextHolder에서 직접 User ID를 가져오는 기존 방식 유지
        // 이 방식이 문제가 없다면 유지하고, 만약 이 부분에서도 principal.name 에러가 나면
        // 이 메서드에도 currentUserId 파라미터를 추가하고 컨트롤러에서 주입하는 방식으로 변경 고려
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS, "채팅방 목록 조회를 위한 인증 정보가 없습니다.");
        }
        Long currentUserId = Long.parseLong(authentication.getName()); // principal.getName()은 userId (String)를 반환

        Page<Chat> chatPage = chatRepository.findByBuyer_UserIdOrSeller_UserId(currentUserId, currentUserId, pageable);

        List<ChatDTO.ChatListResponse.ChatInfo> chatInfoList = chatPage.getContent().stream()
                .map(chat -> {
                    List<Message> messages = messageRepository.findByChat_ChatIdOrderByCreatedAtDesc(chat.getChatId());
                    String lastMessageContent = messages.isEmpty() ? "메시지가 없습니다." : messages.get(0).getContent();
                    return ChatDTO.ChatListResponse.ChatInfo.fromEntity(chat,lastMessageContent, currentUserId);
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
     * 채팅방 참여자만 삭제할 수 있습니다.
     *
     * @param chatId 삭제할 채팅방 ID
     * @param currentUserId 현재 로그인한 사용자 ID (컨트롤러에서 주입받음)
     */
    public void deleteChat(Long chatId, Long currentUserId) { // currentUserId 파라미터 추가
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHATROOM_NOT_FOUND, chatId));

        // **인가 로직을 서비스 메서드 내부로 이동**
        if (!chat.getBuyer().getUserId().equals(currentUserId) && !chat.getSeller().getUserId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.CHAT_FORBIDDEN_ACCESS);
        }

        chatRepository.delete(chat);
    }
}