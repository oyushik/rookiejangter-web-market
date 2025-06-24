package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.ChatDTO;
import com.miniproject.rookiejangter.dto.MessageDTO;
import com.miniproject.rookiejangter.entity.*;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.*;
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
@Transactional // 트랜잭션 관리 유지
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MessageService messageService;
    private final ReservationRepository reservationRepository;

    public ChatDTO.Response createChat(ChatDTO.Request request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long buyerId = Long.parseLong(authentication.getName());
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, buyerId));

        User seller = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, request.getSellerId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, request.getProductId()));

        Optional<Chat> existingChat = chatRepository.
                findByBuyer_UserIdAndSeller_UserIdAndProduct_ProductId
                        (buyerId, request.getSellerId(), request.getProductId());

        if (existingChat.isPresent()) {
            return ChatDTO.Response.fromEntity(existingChat.get());
        }

        Chat chat = Chat.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .build();

        Chat savedChat = chatRepository.save(chat);

        String initialMessageContent = "안녕하세요. '" + product.getTitle() + "' 상품 구매 문의드립니다.";
        MessageDTO.Request initialMessageRequest = MessageDTO.Request.builder()
                .content(initialMessageContent)
                .build();
        messageService.sendMessage(savedChat.getChatId(), initialMessageRequest, buyer.getUserId());

        return ChatDTO.Response.fromEntity(savedChat); // 수정된 ChatDTO.Response.fromEntity 호출
    }


    /**
     * 특정 ID의 채팅방 정보를 조회합니다.
     * 채팅방 참여자만 조회할 수 있습니다.
     *
     * @param chatId 조회할 채팅방 ID
     * @param currentUserId 현재 로그인한 사용자 ID (컨트롤러에서 주입받음)
     * @return 조회된 채팅방 정보
     */
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public ChatDTO.Response getChatById(Long chatId, Long currentUserId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHATROOM_NOT_FOUND, chatId));

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
    @Transactional(readOnly = true) // 읽기 전용 트랜잭션
    public ChatDTO.ChatListResponse getChatsByUserId(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS, "채팅방 목록 조회를 위한 인증 정보가 없습니다.");
        }
        Long currentUserId = Long.parseLong(authentication.getName());

        Page<Chat> chatPage = chatRepository.findByBuyer_UserIdOrSeller_UserId(currentUserId, currentUserId, pageable);

        List<ChatDTO.ChatListResponse.ChatInfo> chatInfoList = chatPage.getContent().stream()
                .map(chat -> {
                    List<Message> messages = messageRepository.findByChat_ChatIdOrderByCreatedAtDesc(chat.getChatId());
                    String lastMessageContent = messages.isEmpty() ? "메시지가 없습니다." : messages.get(0).getContent();
                    return ChatDTO.ChatListResponse.ChatInfo.fromEntity(chat,lastMessageContent, currentUserId); // 수정된 ChatInfo.fromEntity 호출
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
    @Transactional
    public void deleteChat(Long chatId, Long currentUserId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHATROOM_NOT_FOUND, chatId));

        if (!chat.getBuyer().getUserId().equals(currentUserId) && !chat.getSeller().getUserId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.CHAT_FORBIDDEN_ACCESS);
        }

        if (chat.getReservation() != null) {
            throw new BusinessException(ErrorCode.RESERVATION_REMAIN_CANNOT_DELETE);
        }

        chatRepository.delete(chat);
    }


    /**
     * 특정 채팅방에 예약을 설정합니다.
     *
     * @param chatId        예약을 연결할 채팅방 ID
     * @param reservationId 연결할 예약 ID
     * @param currentUserId 현재 로그인한 사용자 ID
     */
    @Transactional
    public void assignChatReservation(Long chatId, Long reservationId, Long currentUserId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHATROOM_NOT_FOUND, chatId));

        if (!chat.getSeller().getUserId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.CHAT_FORBIDDEN_ACCESS);
        }

        Reservation reservation = null;
        if (reservationId != null) {
            reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND, reservationId));
        }
        chat.assignReservation(reservation); // reservationId가 null이면 reservation도 null이 되어 채팅방 예약 초기화
        chatRepository.save(chat);
    }
}