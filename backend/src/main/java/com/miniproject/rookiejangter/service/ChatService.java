package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.ChatDTO;
import com.miniproject.rookiejangter.entity.Chat;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.ChatRepository;
import com.miniproject.rookiejangter.repository.ProductRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * 채팅방을 생성합니다.
     *
     * @param request 채팅방 생성 요청 정보
     * @return 생성된 채팅방 정보
     */
    public ChatDTO.Response createChat(ChatDTO.Request request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, request.getProductId()));

        User buyer = userRepository.findById(request.getSellerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, request.getSellerId()));

        // TODO: 현재 로그인한 사용자를 seller로 설정 (임시)
        User seller = userRepository.findById(1L)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, 1L));

        Chat chat = Chat.builder()
                .product(product)
                .buyer(buyer)
                .seller(seller)
                .build();

        Chat savedChat = chatRepository.save(chat);
        return ChatDTO.Response.fromEntity(savedChat);
    }

    /**
     * 특정 채팅방을 ID로 조회합니다.
     *
     * @param chatRoomId 채팅방 ID
     * @return 채팅방 정보
     */
    public ChatDTO.Response getChatById(Long chatRoomId) {
        Chat chat = chatRepository.findById(chatRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHATROOM_NOT_FOUND, chatRoomId));
        return ChatDTO.Response.fromEntity(chat);
    }

    /**
     * 특정 사용자의 채팅방 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 채팅방 목록 정보
     */
    public ChatDTO.ChatListResponse getChatsByUserId(Long userId) {
        List<Chat> buyerChats = chatRepository.findByBuyer_UserId(userId);
        List<Chat> sellerChats = chatRepository.findBySeller_UserId(userId);

        List<ChatDTO.ChatListResponse.ChatInfo> chatInfoList =
                Stream.concat(buyerChats.stream(), sellerChats.stream())
                .map(chat -> ChatDTO.ChatListResponse.ChatInfo.fromEntity(chat, "temp", 0))
                .collect(Collectors.toList());

        //TODO: Pageable 처리 필요
        return ChatDTO.ChatListResponse.builder()
                .page(0)
                .size(chatInfoList.size())
                .totalElements(chatInfoList.size())
                .totalPages(1)
                .first(true)
                .last(true)
                .content(chatInfoList)
                .build();
    }

    /**
     * 특정 채팅방을 삭제합니다.
     *
     * @param chatRoomId 채팅방 ID
     */
    public void deleteChat(Long chatRoomId) {
        chatRepository.deleteById(chatRoomId);
    }
}