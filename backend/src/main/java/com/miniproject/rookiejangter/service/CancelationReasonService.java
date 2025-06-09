package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.CancelationReasonDTO;
import com.miniproject.rookiejangter.entity.CancelationReason;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.CancelationReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CancelationReasonService {

    private final CancelationReasonRepository cancelationReasonRepository;

    /**
     * 특정 취소 사유를 생성합니다.
     *
     * @param request 취소 사유 요청 정보
     * @return 생성된 취소 사유 정보
     */
    public CancelationReasonDTO.Response createCancelationReason(CancelationReasonDTO.Request request) {
        CancelationReason cancelationReason = CancelationReason.builder()
                .cancelationReasonType(request.getCancelationReasonType())
                .build();

        CancelationReason savedCancelationReason = cancelationReasonRepository.save(cancelationReason);
        return CancelationReasonDTO.Response.fromEntity(savedCancelationReason);
    }

    /**
     * 특정 취소 사유를 ID로 조회합니다.
     *
     * @param cancelationReasonId 취소 사유 ID
     * @return 취소 사유 정보
     */
    public CancelationReasonDTO.Response getCancelationReasonById(Integer cancelationReasonId) {
        CancelationReason cancelationReason = cancelationReasonRepository.findById(cancelationReasonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "CancelationReason", cancelationReasonId));

        return CancelationReasonDTO.Response.fromEntity(cancelationReason);
    }

    /**
     * 모든 취소 사유를 조회합니다.
     *
     * @return 취소 사유 리스트
     */
    public List<CancelationReasonDTO.Response> getAllCancelationReasons() {
        List<CancelationReason> cancelationReasons = cancelationReasonRepository.findAll();

        return cancelationReasons.stream()
                .map(CancelationReasonDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 취소 사유를 업데이트합니다.
     *
     * @param cancelationReasonId 취소 사유 ID
     * @param request 취소 사유 요청 정보
     * @return 업데이트된 취소 사유 정보
     */
    public CancelationReasonDTO.Response updateCancelationReason(Integer cancelationReasonId, CancelationReasonDTO.Request request) {
        CancelationReason cancelationReason = cancelationReasonRepository.findById(cancelationReasonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "CancelationReason", cancelationReasonId));
        cancelationReason.changeReasonType(request.getCancelationReasonType());
        
        return CancelationReasonDTO.Response.fromEntity(cancelationReason);
    }

    /**
     * 특정 취소 사유를 삭제합니다.
     *
     * @param cancelationReasonId 취소 사유 ID
     */
    public void deleteCancelationReason(Integer cancelationReasonId) {
        CancelationReason cancelationReason = cancelationReasonRepository.findById(cancelationReasonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "CancelationReason", cancelationReasonId));

        cancelationReasonRepository.delete(cancelationReason);
    }
}