package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.ReportReasonDTO;
import com.miniproject.rookiejangter.entity.ReportReason;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.ReportReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportReasonService {

    private final ReportReasonRepository reportReasonRepository;

    /**
     * 특정 신고 사유를 생성합니다.
     *
     * @param request 신고 사유 요청 정보
     * @return 생성된 신고 사유 정보
     */
    public ReportReasonDTO.Response createReportReason(ReportReasonDTO.Request request) {
        ReportReason reportReason = ReportReason.builder()
                .reportReasonType(request.getReportReasonType())
                .build();

        ReportReason savedReportReason = reportReasonRepository.save(reportReason);
        return ReportReasonDTO.Response.fromEntity(savedReportReason);
    }

    /**
     * 특정 신고 사유를 ID로 조회합니다.
     *
     * @param reportReasonId 신고 사유 ID
     * @return 신고 사유 정보
     */
    public ReportReasonDTO.Response getReportReasonById(Integer reportReasonId) {
        ReportReason reportReason = reportReasonRepository.findById(reportReasonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "ReportReason", reportReasonId, ""));

        return ReportReasonDTO.Response.fromEntity(reportReason);
    }

    /**
     * 모든 신고 사유를 조회합니다.
     *
     * @return 신고 사유 리스트
     */
    public List<ReportReasonDTO.Response> getAllReportReasons() {
        List<ReportReason> reportReasons = reportReasonRepository.findAll();

        return reportReasons.stream()
                .map(ReportReasonDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 신고 사유를 수정합니다.
     *
     * @param reportReasonId 신고 사유 ID
     * @param request 수정 요청 정보
     * @return 수정된 신고 사유 정보
     */
    public ReportReasonDTO.Response updateReportReason(Integer reportReasonId, ReportReasonDTO.Request request) {
        ReportReason reportReason = reportReasonRepository.findById(reportReasonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "ReportReason", reportReasonId, ""));

        reportReason.changeReasonType(request.getReportReasonType());

        return ReportReasonDTO.Response.fromEntity(reportReason);
    }

    /**
     * 특정 신고 사유를 삭제합니다.
     *
     * @param reportReasonId 신고 사유 ID
     */
    public void deleteReportReason(Integer reportReasonId) {
        ReportReason reportReason = reportReasonRepository.findById(reportReasonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "ReportReason", reportReasonId, ""));

        reportReasonRepository.deleteById(reportReasonId);
    }
}