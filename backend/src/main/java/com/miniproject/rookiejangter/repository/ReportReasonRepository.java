package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.ReportReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportReasonRepository extends JpaRepository<ReportReason, Integer> {
    ReportReason findByReportReasonId(Integer reportReasonId);
    ReportReason findByReportReasonType(String reportReasonType);
}