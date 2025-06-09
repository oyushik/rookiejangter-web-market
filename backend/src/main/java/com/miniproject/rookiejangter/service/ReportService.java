package com.miniproject.rookiejangter.service;

import com.miniproject.rookiejangter.dto.ReportDTO;
import com.miniproject.rookiejangter.entity.Report;
import com.miniproject.rookiejangter.entity.ReportReason;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import com.miniproject.rookiejangter.repository.ReportReasonRepository;
import com.miniproject.rookiejangter.repository.ReportRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportReasonRepository reportReasonRepository;
    private final UserRepository userRepository;

    /**
     * 신고를 생성합니다.
     *
     * @param request 신고 요청 정보
     * @param userId 신고를 생성한 사용자 ID
     * @return 생성된 신고 정보
     */
    public ReportDTO.Response createReport(ReportDTO.Request request, Long userId) {
        ReportReason reportReason = reportReasonRepository.findByReportReasonId(request.getReportReasonId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "ReportReason", request.getReportReasonId(), ""));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, userId));

        Report report = Report.builder()
                .reportReason(reportReason)
                .user(user)
                .targetId(request.getTargetId())
                .targetType(request.getTargetType())
                .reportDetail(request.getReportDetail())
                .isProcessed(false)
                .build();

        Report savedReport = reportRepository.save(report);
        return ReportDTO.Response.fromEntity(savedReport);
    }

    /**
     * 특정 신고를 ID로 조회합니다.
     *
     * @param reportId 신고 ID
     * @return 조회된 신고 정보
     */
    public ReportDTO.Response getReportById(Long reportId) {
        Report report = reportRepository.findByReportId(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Report", reportId, ""));

        return ReportDTO.Response.fromEntity(report);
    }

    /**
     * 특정 사용자가 생성한 신고 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자가 생성한 신고 목록
     */
    public List<ReportDTO.Response> getReportsByUserId(Long userId) {
        List<Report> reports = reportRepository.findByUser_UserId(userId);

        return reports.stream()
                .map(ReportDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 처리되지 않은 신고 목록을 조회합니다.
     *
     * @return 처리되지 않은 신고 목록
     */
    public List<ReportDTO.Response> getUnprocessedReports() {
        List<Report> reports = reportRepository.findByIsProcessedFalse();

        return reports.stream()
                .map(ReportDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 신고를 수정합니다.
     *
     * @param reportId 신고 ID
     * @param request 수정 요청 정보
     * @return 수정된 신고 정보
     */
    public ReportDTO.Response updateReport(Long reportId, ReportDTO.Request request) {
        Report report = reportRepository.findByReportId(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Report", reportId, ""));

        ReportReason reportReason = reportReasonRepository.findByReportReasonId(request.getReportReasonId())
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "ReportReason", request.getReportReasonId(), ""));

        report.updateReportInfo(reportReason, request.getTargetId(), request.getTargetType(), request.getReportDetail());

        return ReportDTO.Response.fromEntity(report);
    }

    /**
     * 특정 신고를 삭제합니다.
     *
     * @param reportId 신고 ID
     */
    public void markReportAsProcessed(Long reportId) {
        Report report = reportRepository.findByReportId(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "Report", reportId, ""));

        report.markAsProcessed();
        reportRepository.save(report);
    }
}