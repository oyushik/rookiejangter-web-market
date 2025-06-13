package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByReportReason_ReportReasonId(Integer reportReasonReportReasonId, Pageable pageable);
    List<Report> findByUser_UserId(Long userUserId);
    List<Report> findByTargetId(Long targetId);
    List<Report> findByTargetType(String targetType);
    List<Report> findByIsProcessedFalse();
    List<Report> findByIsProcessedTrue();
}