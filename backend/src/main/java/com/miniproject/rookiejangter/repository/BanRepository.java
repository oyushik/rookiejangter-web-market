package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Ban;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BanRepository extends JpaRepository<Ban, Long> {
    Ban findByBanId(Long banId);
    List<Ban> findByUser_UserId(Long userUserId);
    Ban findByReport_ReportId(Long reportReportId);
    List<Ban> findByBanReason(String banReason);
}