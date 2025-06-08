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

    public ReportReasonDTO.Response createReportReason(ReportReasonDTO.Request request) {
        ReportReason reportReason = ReportReason.builder()
                .reportReasonType(request.getReportReasonType())
                .build();

        ReportReason savedReportReason = reportReasonRepository.save(reportReason);
        return ReportReasonDTO.Response.fromEntity(savedReportReason);
    }

    public ReportReasonDTO.Response getReportReasonById(Integer reportReasonId) {
        ReportReason reportReason = reportReasonRepository.findByReportReasonId(reportReasonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "ReportReason", reportReasonId, ""));

        return ReportReasonDTO.Response.fromEntity(reportReason);
    }

    public List<ReportReasonDTO.Response> getAllReportReasons() {
        List<ReportReason> reportReasons = reportReasonRepository.findAll();

        return reportReasons.stream()
                .map(ReportReasonDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    public ReportReasonDTO.Response updateReportReason(Integer reportReasonId, ReportReasonDTO.Request request) {
        ReportReason reportReason = reportReasonRepository.findByReportReasonId(reportReasonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "ReportReason", reportReasonId, ""));

        reportReason.setReportReasonType(request.getReportReasonType());

        ReportReason updatedReportReason = reportReasonRepository.save(reportReason);
        return ReportReasonDTO.Response.fromEntity(updatedReportReason);
    }

    public void deleteReportReason(Integer reportReasonId) {
        ReportReason reportReason = reportReasonRepository.findByReportReasonId(reportReasonId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "ReportReason", reportReasonId, ""));

        reportReasonRepository.deleteById(reportReasonId);
    }
}