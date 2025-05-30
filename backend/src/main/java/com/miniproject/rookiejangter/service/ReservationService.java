package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.controller.dto.ReservationDTO;
import com.miniproject.rookiejangter.entity.Post;
import com.miniproject.rookiejangter.entity.Reservation;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.repository.PostRepository;
import com.miniproject.rookiejangter.repository.ReservationRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final PostRepository postRepository;
    private final PostService postService;

    @Transactional
    public ReservationDTO.Response createReservation(Long buyerId, Long postId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("구매자를 찾을 수 없습니다: " + buyerId));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));

        if (post.getIsCompleted() || (post.getIsReserved() && !post.getUser().getUserId().equals(buyerId))) {
            throw new IllegalStateException("이미 판매 완료되었거나 다른 사용자에 의해 예약된 게시글입니다.");
        }

        if (post.getUser().getUserId().equals(buyerId)) {
            throw new IllegalArgumentException("자신의 게시글은 예약할 수 없습니다.");
        }

        if (reservationRepository.existsByBuyer_UserIdAndPost_PostId(buyerId, postId)) { //
            throw new IllegalStateException("이미 해당 게시글에 대한 예약 요청이 존재합니다.");
        }

        User seller = post.getUser();

        Reservation reservation = Reservation.builder()
                .buyer(buyer)
                .seller(seller)
                .post(post)
                .status(Reservation.TradeStatus.REQUESTED) //
                .isCanceled(false)
                .build();
        Reservation savedReservation = reservationRepository.save(reservation);

        postService.updatePostStatus(postId, true, null, seller.getUserId());


        return ReservationDTO.Response.fromEntity(savedReservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO.Response> getReservationsByBuyer(Long buyerId) {
        userRepository.findById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("구매자를 찾을 수 없습니다: " + buyerId));
        return reservationRepository.findByBuyer_UserId(buyerId).stream() //
                .map(ReservationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO.Response> getReservationsBySeller(Long sellerId) {
        userRepository.findById(sellerId)
                .orElseThrow(() -> new EntityNotFoundException("판매자를 찾을 수 없습니다: " + sellerId));
        return reservationRepository.findBySeller_UserId(sellerId).stream() //
                .map(ReservationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO.Response> getReservationsByPost(Long postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: " + postId));
        return reservationRepository.findByPost_PostId(postId).stream() //
                .map(ReservationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationDTO.Response getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약 정보를 찾을 수 없습니다: " + reservationId));
        return ReservationDTO.Response.fromEntity(reservation);
    }


    @Transactional
    public ReservationDTO.Response updateReservationStatus(Long reservationId, Reservation.TradeStatus newStatus, Long currentUserId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약 정보를 찾을 수 없습니다: " + reservationId));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + currentUserId));

        boolean isSeller = reservation.getSeller().getUserId().equals(currentUserId);
        boolean isBuyer = reservation.getBuyer().getUserId().equals(currentUserId);

        Post post = reservation.getPost();

        switch (newStatus) {
            case ACCEPTED:
                if (!isSeller) {
                    throw new SecurityException("예약 수락 권한이 없습니다.");
                }
                reservation.setStatus(Reservation.TradeStatus.ACCEPTED); //
                post.setIsReserved(true);
                postRepository.save(post);
                break;
            case DECLINED:
                if (!isSeller) {
                    throw new SecurityException("예약 거절 권한이 없습니다.");
                }
                reservation.setStatus(Reservation.TradeStatus.DECLINED); //
                post.setIsReserved(false);
                postRepository.save(post);
                break;
            case CANCELLED:
                if (!isBuyer && !isSeller) {
                    throw new SecurityException("예약 취소 권한이 없습니다.");
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
                    throw new IllegalStateException("현재 상태에서는 예약을 취소할 수 없습니다.");
                }
                post.setIsReserved(false);
                postRepository.save(post);
                break;
            case COMPLETED:
                if (!isSeller) {
                    throw new SecurityException("거래 완료 처리 권한이 없습니다.");
                }
                if (reservation.getStatus() != Reservation.TradeStatus.ACCEPTED) {
                    throw new IllegalStateException("수락된 예약만 완료 처리할 수 있습니다.");
                }
                reservation.setStatus(Reservation.TradeStatus.COMPLETED); //
                post.setIsCompleted(true);
                post.setIsReserved(false); // 예약 상태 해제
                postRepository.save(post);
                // 여기에 CompleteService를 호출하여 거래 완료 기록 생성 로직 추가 가능
                break;
            default:
                throw new IllegalArgumentException("유효하지 않은 상태 변경 요청입니다: " + newStatus);
        }

        Reservation updatedReservation = reservationRepository.save(reservation);
        return ReservationDTO.Response.fromEntity(updatedReservation);
    }

    @Transactional
    public void deleteReservation(Long reservationId, Long currentUserId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("예약 정보를 찾을 수 없습니다: " + reservationId));

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + currentUserId));

        boolean isBuyer = reservation.getBuyer().getUserId().equals(currentUserId);

        if (!isBuyer) {
            throw new SecurityException("예약 기록 삭제 권한이 없습니다. 본인이 요청한 예약만 삭제할 수 있습니다.");
        }

        // 일반적으로 예약 기록은 soft delete 하거나 상태 변경으로 관리하지만, 물리적 삭제가 필요하다면 아래 로직 사용
        // 예약 요청 상태(REQUESTED)이거나 거절(DECLINED), 취소(CANCELLED)된 경우에만 구매자가 삭제 가능하도록 제한할 수 있음
        if (reservation.getStatus() == Reservation.TradeStatus.REQUESTED ||
                reservation.getStatus() == Reservation.TradeStatus.DECLINED ||
                reservation.getStatus() == Reservation.TradeStatus.CANCELLED) {

            // 게시글의 예약 상태를 풀어줄 필요가 있는지 확인 (보통 REQUESTED 상태에서는 isReserved가 false일 것임)
            if (reservation.getPost().getIsReserved() && reservation.getStatus() == Reservation.TradeStatus.REQUESTED) {
                // 이 경우는 드물지만, 방어적으로 코딩
            }
            reservationRepository.delete(reservation);
        } else {
            throw new IllegalStateException("현재 상태의 예약은 삭제할 수 없습니다. 취소 후 시도해주세요.");
        }
    }
}