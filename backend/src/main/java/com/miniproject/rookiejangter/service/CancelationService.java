package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.CancelationDTO;
import com.miniproject.rookiejangter.entity.Cancelation;
import com.miniproject.rookiejangter.entity.CancelationReason;
import com.miniproject.rookiejangter.entity.Reservation;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.CancelationReasonRepository;
import com.miniproject.rookiejangter.repository.CancelationRepository;
import com.miniproject.rookiejangter.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CancelationService {

    private final CancelationRepository cancelationRepository;
    private final CancelationReasonRepository cancelationReasonRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 예약 취소를 생성합니다.
     *
     * @param reservationId 예약 ID
     * @param request       취소 요청 정보
     * @return 생성된 취소 정보
     */
    public CancelationDTO.Response createCancelation(Long reservationId, CancelationDTO.Request request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRADE_NOT_FOUND, reservationId));

        CancelationReason cancelationReason = cancelationReasonRepository.findById(request.getCancelationReasonId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "CancelationReason", "ID", request.getCancelationReasonId()));

        Cancelation cancelation = Cancelation.builder()
                .reservation(reservation)
                .cancelationReason(cancelationReason)
                .cancelationDetail(request.getCancelationDetail())
                .canceledAt(LocalDateTime.now())
                .build();

        Cancelation savedCancelation = cancelationRepository.save(cancelation);
        return CancelationDTO.Response.fromEntity(savedCancelation);
    }

    /**
     * 특정 예약 ID에 대한 취소 정보를 조회합니다.
     *
     * @param reservationId 예약 ID
     * @return 취소 정보
     */
    public CancelationDTO.Response getCancelationByReservationId(Long reservationId) {
        Cancelation cancelation = cancelationRepository.findByReservation_ReservationId(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRADE_NOT_FOUND, reservationId));

        return CancelationDTO.Response.fromEntity(cancelation);
    }

    /**
     * 특정 취소 사유 ID에 대한 모든 취소 정보를 조회합니다.
     *
     * @param cancelationReasonId 취소 사유 ID
     * @return 취소 정보 리스트
     */
    public List<CancelationDTO.Response> getCancelationsByCancelationReasonId(Integer cancelationReasonId) {
        List<Cancelation> cancelations = cancelationRepository.findByCancelationReason_CancelationReasonId(cancelationReasonId);

        return cancelations.stream()
                .map(CancelationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 예약 ID에 대한 취소 정보를 업데이트합니다.
     *
     * @param reservationId 예약 ID
     * @param request       취소 요청 정보
     * @return 업데이트된 취소 정보
     */
    public CancelationDTO.Response updateCancelation(Long reservationId, CancelationDTO.Request request) {
        Cancelation cancelation = cancelationRepository.findByReservation_ReservationId(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRADE_NOT_FOUND, reservationId));

        CancelationReason cancelationReason = cancelationReasonRepository.findById(request.getCancelationReasonId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "CancelationReason", "ID", request.getCancelationReasonId()));

        cancelation.updateCancelationInfo(cancelationReason, request.getCancelationDetail());
        return CancelationDTO.Response.fromEntity(cancelation);
    }

    public void deleteCancelation(Long reservationId) {
        Cancelation cancelation = cancelationRepository.findByReservation_ReservationId(reservationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRADE_NOT_FOUND, reservationId));

        cancelationRepository.delete(cancelation);
    }
}