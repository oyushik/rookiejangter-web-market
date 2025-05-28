package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.ReportReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportReasonRepository extends JpaRepository<ReportReason, Integer> {
    Optional<ReportReason> findByReportReasonId(Integer reportReasonId);
}