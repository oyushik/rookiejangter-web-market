package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findByReportId(Long reportId);
    List<Report> findByReportReason_ReportReasonId(Integer reportReasonReportReasonId);
    List<Report> findByUser_UserId(Long userUserId);
    List<Report> findByTargetId(Long targetId);
    List<Report> findByTargetType(String targetType);
    List<Report> findByIsProcessed(Boolean isProcessed);
}