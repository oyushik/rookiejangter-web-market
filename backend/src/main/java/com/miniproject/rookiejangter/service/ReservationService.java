package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.CancelationDTO;
import com.miniproject.rookiejangter.dto.ReservationDTO;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.Reservation;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.entity.Chat;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.ChatRepository;
import com.miniproject.rookiejangter.repository.ProductRepository;
import com.miniproject.rookiejangter.repository.ReservationRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ChatService chatService;
    private final ChatRepository chatRepository;
    private final CancelationService cancelationService;

    /**
     * 예약을 생성합니다.
     *
     * @param buyerId   구매자 ID
     * @param sellerId   판매자 ID
     * @param productId 상품 ID
     * @param chatId    채팅방 ID
     * @return 생성된 예약 정보
     */
    @Transactional
    public ReservationDTO.Response createReservation(Long buyerId, Long sellerId, Long productId, Long chatId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "구매자를 찾을 수 없습니다. ID: " + buyerId));
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "판매자를 찾을 수 없습니다. ID: " + sellerId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, "상품을 찾을 수 없습니다. ID: " + productId));

        // 이미 예약 중이거나 판매 완료된 상품인지 확인
        if (product.getIsReserved() || product.getIsCompleted()) {
            throw new BusinessException(ErrorCode.RESERVATION_ALREADY_EXISTS, "이미 예약 중이거나 판매 완료된 상품입니다.");
        }

        // 해당 채팅방에 이미 예약이 있는지 확인 (중복 예약 방지)
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHATROOM_NOT_FOUND, "채팅방을 찾을 수 없습니다. ID: " + chatId));
        if (chat.getReservation() != null) {
            throw new BusinessException(ErrorCode.RESERVATION_ALREADY_EXISTS, "해당 채팅방에 이미 예약이 존재합니다.");
        }

        Reservation reservation = Reservation.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .chat(chat)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        // 상품 상태를 '예약 중'으로 변경
        productService.updateProductStatus(productId, true, false, sellerId);

        // 채팅방에 예약 정보 할당
        chatService.assignChatReservation(chatId, savedReservation.getReservationId(), sellerId);

        return ReservationDTO.Response.fromEntity(savedReservation);
    }

    /**
     * 예약 ID에 대한 예약 정보를 조회합니다.
     *
     * @param reservationId 예약 ID
     * @return 예약 정보
     */
    @Transactional(readOnly = true)
    public ReservationDTO.Response getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND, reservationId));
        return ReservationDTO.Response.fromEntity(reservation);
    }

    /**
     * 현재 로그인 중인 사용자가 buyerId로 설정된 모든 reservations를 목록으로 표시
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO.Response> getReservationsByBuyer(Long currentUserId) {
        List<Reservation> reservations = reservationRepository.findByBuyer_UserId(currentUserId);
        return reservations.stream()
                .map(ReservationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 현재 로그인 중인 사용자가 sellerId로 설정된 모든 reservations를 목록으로 표시
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO.Response> getReservationsBySeller(Long currentUserId) {
        List<Reservation> reservations = reservationRepository.findBySeller_UserId(currentUserId);
        return reservations.stream()
                .map(ReservationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 예약을 취소(삭제)합니다.
     * (이전 deleteReservation 메서드의 시그니처와 로직을 수정)
     *
     * @param currentUserId 현재 사용자 ID (취소를 요청한 사람)
     * @param chatId        예약이 연결된 채팅방 ID
     * @param cancelationRequest 취소 요청 정보 (취소 사유 등)
     */
    @Transactional
    public void deleteReservation(Long currentUserId, Long chatId, CancelationDTO.Request cancelationRequest) {
        // 1. chatId를 사용하여 Chat 엔티티 조회
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHATROOM_NOT_FOUND, "해당 채팅방을 찾을 수 없습니다."));

        // 2. Chat 엔티티에 연결된 Reservation 엔티티 조회
        Reservation reservation = chat.getReservation();
        if (reservation == null) {
            throw new BusinessException(ErrorCode.RESERVATION_NOT_FOUND, "해당 채팅방에 연결된 예약이 없습니다.");
        }

        // 3. 인가 확인: 현재 사용자가 예약의 구매자 또는 판매자인지 확인
        if (!reservation.getBuyer().getUserId().equals(currentUserId) &&
                !reservation.getSeller().getUserId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.RESERVATION_ACTION_FORBIDDEN);
        }

        // 4. 취소 기록 생성
        // CancelationDTO.Request에 필요한 정보를 Reservation에서 추출하여 설정
        cancelationRequest.setProductId(reservation.getProduct().getProductId());
        cancelationRequest.setBuyerId(reservation.getBuyer().getUserId());
        cancelationRequest.setSellerId(reservation.getSeller().getUserId());
        // 누가 취소했는지 isCanceledByBuyer 필드에 반영
        cancelationRequest.setIsCanceledByBuyer(reservation.getBuyer().getUserId().equals(currentUserId));

        cancelationService.createCancelation(cancelationRequest); // 취소 기록 생성

        // 5. 상품 상태 업데이트: '예약 중' 상태를 false로 변경
        productService.updateProductStatus(
                reservation.getProduct().getProductId(),
                false, // isSold = false (취소되었으므로 판매되지 않음)
                false, // isReserved = false (예약 취소)
                reservation.getSeller().getUserId() // 판매자 ID
        );

        // 6. 채팅방에서 예약 정보 할당 해제
        // chatService.assignChatReservation 메서드가 Long reservationId를 받아 null이면 연결 해제하도록 구현되어 있어야 합니다.
        chatService.assignChatReservation(chatId, null, reservation.getSeller().getUserId());

        // 7. 예약 엔티티 삭제
        reservationRepository.delete(reservation);
    }
}