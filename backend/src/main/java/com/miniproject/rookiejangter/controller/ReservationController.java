package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.dto.CancelationDTO;
import com.miniproject.rookiejangter.dto.ProductDTO;
import com.miniproject.rookiejangter.dto.ReservationDTO;
import com.miniproject.rookiejangter.entity.Chat;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.ChatRepository;
import com.miniproject.rookiejangter.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class ReservationController {

    private final ReservationService reservationService;
    private final ChatRepository chatRepository;

    /**
     * 새로운 거래 예약을 생성합니다.
     * 판매자가 상품의 상태를 '예약 중'으로 변경할 때 사용됩니다.
     * 요청된 chatId를 통해 채팅방 정보를 조회하고, 구매자 ID, 상품 ID, 판매자 ID(로그인 사용자)를 추출하여 예약을 생성합니다.
     *
     * @param chatId          예약을 생성할 채팅방 ID (URL 경로에서 추출)
     * @param authentication  현재 로그인한 사용자 정보 (판매자 ID 추출)
     * @return 생성된 예약 정보
     */
    @PostMapping("/{chatId}")
    public ResponseEntity<ProductDTO.ApiResponseWrapper<ReservationDTO.Response>> createReservation(
            @PathVariable("chatId") Long chatId, // URL 경로에서 chatId를 받습니다.
            Authentication authentication) {
        try {
            Long sellerId = Long.parseLong(authentication.getName()); // 현재 로그인한 사용자가 판매자 ID

            // chatId를 사용하여 채팅방 조회
            Chat chat = chatRepository.findById(chatId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.CHATROOM_NOT_FOUND, "해당 채팅방을 찾을 수 없습니다."));

            // 현재 로그인한 사용자가 채팅방의 판매자인지 확인하는 인가 로직
            if (!chat.getSeller().getUserId().equals(sellerId)) {
                throw new BusinessException(ErrorCode.RESERVATION_ACTION_FORBIDDEN, "예약 생성은 해당 상품의 판매자만 가능합니다.");
            }

            // 채팅방 정보에서 필요한 데이터 추출
            Long productId = chat.getProduct().getProductId();
            Long buyerId = chat.getBuyer().getUserId();

            // ReservationService의 createReservation 메서드 호출
            ReservationDTO.Response reservation =
                    reservationService.createReservation(buyerId, sellerId, productId, chatId); // URL에서 받은 chatId 사용

            return ResponseEntity.ok(ProductDTO.ApiResponseWrapper.<ReservationDTO.Response>builder()
                    .success(true)
                    .data(reservation)
                    .message("거래 예약이 성공적으로 생성되었습니다.")
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ProductDTO.ApiResponseWrapper.<ReservationDTO.Response>builder()
                    .success(false)
                    .error(e.getMessage())
                    .message(e.getErrorCode().getMessage())
                    .build());
        } catch (Exception e) {
            System.err.println("예약 생성 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ProductDTO.ApiResponseWrapper.<ReservationDTO.Response>builder()
                    .success(false)
                    .error(e.getMessage())
                    .message("예약 생성 중 알 수 없는 오류가 발생했습니다.")
                    .build());
        }
    }

    // 예약 상세 조회 (필요시)
    @GetMapping("/{reservation_id}")
    public ResponseEntity<ProductDTO.ApiResponseWrapper<ReservationDTO.Response>> getReservationById(
            @PathVariable("reservation_id") Long reservationId) {
        ReservationDTO.Response reservation = reservationService.getReservationById(reservationId);
        return ResponseEntity.ok(ProductDTO.ApiResponseWrapper.<ReservationDTO.Response>builder()
                .success(true)
                .data(reservation)
                .message("예약 상세 정보가 성공적으로 조회되었습니다.")
                .build());
    }

    // 현재 로그인 중인 사용자가 buyerId로 설정된 모든 reservations를 목록으로 표시
    @GetMapping("/buyer")
    public ResponseEntity<ProductDTO.ApiResponseWrapper<List<ReservationDTO.Response>>> getReservationsByBuyer(
            Authentication authentication) {
        Long currentUserId = Long.parseLong(authentication.getName()); // JWT에서 userId 추출
        List<ReservationDTO.Response> reservations = reservationService.getReservationsByBuyer(currentUserId);
        return ResponseEntity.ok(ProductDTO.ApiResponseWrapper.<List<ReservationDTO.Response>>builder()
                .success(true)
                .data(reservations)
                .message("구매자로서 참여한 예약 목록이 성공적으로 조회되었습니다.")
                .build());
    }

    // 현재 로그인 중인 사용자가 sellerId로 설정된 모든 reservations를 목록으로 표시
    @GetMapping("/seller")
    public ResponseEntity<ProductDTO.ApiResponseWrapper<List<ReservationDTO.Response>>> getReservationsBySeller(
            Authentication authentication) {
        Long currentUserId = Long.parseLong(authentication.getName()); // JWT에서 userId 추출
        List<ReservationDTO.Response> reservations = reservationService.getReservationsBySeller(currentUserId);
        return ResponseEntity.ok(ProductDTO.ApiResponseWrapper.<List<ReservationDTO.Response>>builder()
                .success(true)
                .data(reservations)
                .message("판매자로서 참여한 예약 목록이 성공적으로 조회되었습니다.")
                .build());
    }

    /**
     * 특정 채팅방에 연결된 예약을 취소(삭제)합니다.
     *
     * @param chatId          취소할 예약이 연결된 채팅방 ID
     * @param cancelationRequest 취소 사유 등의 정보
     * @param authentication  현재 로그인한 사용자 정보
     * @return 취소 성공 여부
     */
    @DeleteMapping("/{chatId}") // @PathVariable 이름을 "chatId"로 통일했습니다.
    public ResponseEntity<ProductDTO.ApiResponseWrapper<Void>> deleteReservation(
            @PathVariable("chatId") Long chatId, // @PathVariable 이름을 "chatId"로 통일
            @RequestBody CancelationDTO.Request cancelationRequest,
            Authentication authentication) {
        try {
            Long currentUserId = Long.parseLong(authentication.getName());
            // 수정된 ReservationService 메서드 호출
            reservationService.deleteReservation(currentUserId, chatId, cancelationRequest);
            return ResponseEntity.ok(ProductDTO.ApiResponseWrapper.<Void>builder()
                    .success(true)
                    .message("예약이 성공적으로 취소되었습니다.") // 메시지를 "취소"로 변경했습니다.
                    .build());
        } catch (BusinessException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(ProductDTO.ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error(e.getMessage())
                    .message(e.getErrorCode().getMessage())
                    .build());
        } catch (Exception e) {
            System.err.println("예약 취소 중 오류 발생: " + e.getMessage()); // 오류 로그 추가
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ProductDTO.ApiResponseWrapper.<Void>builder()
                    .success(false)
                    .error(e.getMessage())
                    .message("예약 취소 중 알 수 없는 오류가 발생했습니다.")
                    .build());
        }
    }
}