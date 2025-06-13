package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.ReservationDTO;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.Reservation;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.ProductRepository;
import com.miniproject.rookiejangter.repository.ReservationRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final NotificationService notificationService;

    /**
     * 예약을 생성합니다.
     *
     * @param buyerId   구매자 ID
     * @param productId 상품 ID
     * @return 생성된 예약 정보
     */
    @Transactional
    public ReservationDTO.Response createReservation(Long buyerId, Long productId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, buyerId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        if ((product.getIsReserved() && !product.getUser().getUserId().equals(buyerId))) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_RESERVABLE, "이미 거래 예정이거나, 판매가 완료된 상품입니다.");
        }

        if (product.getUser().getUserId().equals(buyerId)) {
            throw new BusinessException(ErrorCode.CANNOT_RESERVE_OWN_PRODUCT);
        }

        if (reservationRepository.existsByBuyer_UserIdAndProduct_ProductIdAndIsCanceled(buyerId, productId, true)) {
            throw new BusinessException(ErrorCode.RESERVATION_ALREADY_EXISTS, String.valueOf(productId));
        }

        User seller = product.getUser();

        Reservation reservation = Reservation.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .isCanceled(false)
                .build();
        Reservation savedReservation = reservationRepository.save(reservation);

        productService.updateProductStatus(productId, true, false, seller.getUserId());

        // 알림 생성
        String notificationMessageToBuyer = seller.getUserName() + "님과의 '" + product.getTitle() + "' 구매 거래가 예약되었습니다.";
        String notificationMessageToSeller = buyer.getUserName() + "님과의 '" + product.getTitle() + "' 판매 거래가 예약되었습니다.";

        notificationService.createNotification(
                buyer.getUserId(),
                reservation.getReservationId(),
                "Reservation",
                notificationMessageToBuyer
        );

        notificationService.createNotification(
                seller.getUserId(),
                reservation.getReservationId(),
                "Reservation",
                notificationMessageToSeller
        );
        return ReservationDTO.Response.fromEntity(savedReservation);
    }

    /**
     * 현재 사용자가 예약을 요청한 상품에 대해 예약을 조회합니다.
     *
     * @param currentUserId 현재 사용자 ID
     * @return 예약 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO.Response> getAllReservations(Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, currentUserId));

         if (!currentUser.getIsAdmin().equals(true)) {
             throw new BusinessException(ErrorCode.ACCESS_DENIED, "관리자만 모든 예약을 조회할 수 있습니다.");
         }

        return reservationRepository.findAll().stream()
                .map(ReservationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 구매자가 요청한 예약을 조회합니다.
     *
     * @param buyerId 구매자 ID
     * @return 예약 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO.Response> getReservationsByBuyer(Long buyerId) {
        userRepository.findById(buyerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, buyerId));
        return reservationRepository.findByBuyer_UserId(buyerId).stream()
                .map(ReservationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 판매자가 등록한 상품에 대한 예약을 조회합니다.
     *
     * @param sellerId 판매자 ID
     * @return 예약 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO.Response> getReservationsBySeller(Long sellerId) {
        userRepository.findById(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, sellerId));
        return reservationRepository.findBySeller_UserId(sellerId).stream()
                .map(ReservationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 상품에 대한 모든 예약을 조회합니다.
     *
     * @param productId 상품 ID
     * @return 예약 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<ReservationDTO.Response> getReservationsByProduct(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));
        return reservationRepository.findByProduct_ProductId(productId).stream()
                .map(ReservationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 예약 ID에 대한 예약 정보를 조회합니다.
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
     * 예약을 삭제합니다.
     *
     * @param reservationId 예약 ID
     * @param currentUserId 현재 사용자 ID
     */
    @Transactional
    public void deleteReservation(Long reservationId, Long currentUserId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND, reservationId));

        Product product = reservation.getProduct();

        Boolean isBuyer = reservation.getBuyer().getUserId().equals(currentUserId);

        if (!isBuyer) {
            throw new BusinessException(ErrorCode.RESERVATION_DELETE_CONDITIONS_NOT_MET, "본인이 요청한 예약이 아닙니다.");
        }

        product.markAsReserved(false);
        reservationRepository.delete(reservation);
    }
}