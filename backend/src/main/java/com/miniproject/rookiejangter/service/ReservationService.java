package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.controller.dto.ReservationDTO;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Transactional
    public ReservationDTO.Response createReservation(Long buyerId, Long productId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, buyerId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));

        if (product.getIsCompleted() || (product.getIsReserved() && !product.getUser().getUserId().equals(buyerId))) {
            throw new BusinessException(ErrorCode.PRODUCT_NOT_RESERVABLE, "이미 판매 완료된 상품입니다.");
        }

        if (product.getUser().getUserId().equals(buyerId)) {
            throw new BusinessException(ErrorCode.CANNOT_RESERVE_OWN_PRODUCT);
        }

        if (reservationRepository.existsByBuyer_UserIdAndProduct_ProductId(buyerId, productId)) { //
            throw new BusinessException(ErrorCode.RESERVATION_ALREADY_EXISTS, String.valueOf(productId));
        }

        User seller = product.getUser();

        Reservation reservation = Reservation.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .status(Reservation.TradeStatus.REQUESTED) //
                .isCanceled(false)
                .build();
        Reservation savedReservation = reservationRepository.save(reservation);

        productService.updateProductStatus(productId, true, null, seller.getUserId());


        return ReservationDTO.Response.fromEntity(savedReservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO.Response> getReservationsByBuyer(Long buyerId) {
        userRepository.findById(buyerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, buyerId));
        return reservationRepository.findByBuyer_UserId(buyerId).stream() //
                .map(ReservationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO.Response> getReservationsBySeller(Long sellerId) {
        userRepository.findById(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, sellerId));
        return reservationRepository.findBySeller_UserId(sellerId).stream() //
                .map(ReservationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO.Response> getReservationsByProduct(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND, productId));
        return reservationRepository.findByProduct_ProductId(productId).stream() //
                .map(ReservationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationDTO.Response getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND, reservationId));
        return ReservationDTO.Response.fromEntity(reservation);
    }


    @Transactional
    public ReservationDTO.Response updateReservationStatus(Long reservationId, Reservation.TradeStatus newStatus, Long currentUserId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND, reservationId));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, currentUserId));

        boolean isSeller = reservation.getSeller().getUserId().equals(currentUserId);
        boolean isBuyer = reservation.getBuyer().getUserId().equals(currentUserId);

        Product product = reservation.getProduct();

        switch (newStatus) {
            case ACCEPTED:
                if (!isSeller) {
                    throw new BusinessException(ErrorCode.RESERVATION_ACTION_FORBIDDEN, "수락");
                }
                reservation.setStatus(Reservation.TradeStatus.ACCEPTED); //
                product.setIsReserved(true);
                productRepository.save(product);
                break;
            case DECLINED:
                if (!isSeller) {
                    throw new BusinessException(ErrorCode.RESERVATION_ACTION_FORBIDDEN, "거절");
                }
                reservation.setStatus(Reservation.TradeStatus.DECLINED); //
                product.setIsReserved(false);
                productRepository.save(product);
                break;
            case CANCELLED:
                if (!isBuyer && !isSeller) {
                    throw new BusinessException(ErrorCode.RESERVATION_ACTION_FORBIDDEN, "취소");
                }
                // 구매자는 REQUESTED 또는 ACCEPTED 상태에서 취소 가능
                // 판매자는 ACCEPTED 상태에서 취소 가능 (구매자와 합의 하에)
                if (isBuyer && (reservation.getStatus() == Reservation.TradeStatus.REQUESTED || reservation.getStatus() == Reservation.TradeStatus.ACCEPTED)) {
                    reservation.setStatus(Reservation.TradeStatus.CANCELLED); //
                    reservation.setIsCanceled(true); //
                } else if (isSeller && reservation.getStatus() == Reservation.TradeStatus.ACCEPTED) {
                    reservation.setStatus(Reservation.TradeStatus.CANCELLED); //
                    reservation.setIsCanceled(true); //
                } else {
                    throw new BusinessException(ErrorCode.RESERVATION_INVALID_STATE_FOR_ACTION, currentUser.getUserId(), "취소");
                }
                product.setIsReserved(false);
                productRepository.save(product);
                break;
            case COMPLETED:
                if (!isSeller) {
                    throw new BusinessException(ErrorCode.RESERVATION_ACTION_FORBIDDEN, "완료");
                }
                if (reservation.getStatus() != Reservation.TradeStatus.ACCEPTED) {
                    throw new BusinessException(ErrorCode.RESERVATION_INVALID_STATE_FOR_ACTION, currentUser.getUserId(), "완료");
                }
                reservation.setStatus(Reservation.TradeStatus.COMPLETED); //
                product.setIsCompleted(true);
                product.setIsReserved(false); // 예약 상태 해제
                productRepository.save(product);
                // 여기에 CompleteService를 호출하여 거래 완료 기록 생성 로직 추가 가능
                break;
            default:
                throw new BusinessException(ErrorCode.RESERVATION_INVALID_STATUS_TRANSITION, newStatus.name());
        }

        Reservation updatedReservation = reservationRepository.save(reservation);
        return ReservationDTO.Response.fromEntity(updatedReservation);
    }

    @Transactional
    public void deleteReservation(Long reservationId, Long currentUserId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESERVATION_NOT_FOUND, reservationId));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, currentUserId));

        boolean isBuyer = reservation.getBuyer().getUserId().equals(currentUserId);

        if (!isBuyer) {
            throw new BusinessException(ErrorCode.RESERVATION_DELETE_CONDITIONS_NOT_MET, "본인이 요청한 예약이 아닙니다.");
        }

        // 일반적으로 예약 기록은 soft delete 하거나 상태 변경으로 관리하지만, 물리적 삭제가 필요하다면 아래 로직 사용
        // 예약 요청 상태(REQUESTED)이거나 거절(DECLINED), 취소(CANCELLED)된 경우에만 구매자가 삭제 가능하도록 제한할 수 있음
        if (reservation.getStatus() == Reservation.TradeStatus.REQUESTED ||
                reservation.getStatus() == Reservation.TradeStatus.DECLINED ||
                reservation.getStatus() == Reservation.TradeStatus.CANCELLED) {

            // 게시글의 예약 상태를 풀어줄 필요가 있는지 확인 (보통 REQUESTED 상태에서는 isReserved가 false일 것임)
            if (reservation.getProduct().getIsReserved() && reservation.getStatus() == Reservation.TradeStatus.REQUESTED) {
                // 이 경우는 드물지만, 방어적으로 코딩
            }
            reservationRepository.delete(reservation);
        } else {
            throw new BusinessException(ErrorCode.RESERVATION_DELETE_CONDITIONS_NOT_MET, "현재 상태(" + reservation.getStatus() + ")의 예약은 삭제할 수 없습니다.");
        }
    }
}