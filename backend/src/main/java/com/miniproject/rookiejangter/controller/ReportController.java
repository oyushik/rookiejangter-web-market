package com.miniproject.rookiejangter.controller;

import com.miniproject.rookiejangter.controller.dto.BanDTO;
import com.miniproject.rookiejangter.controller.dto.ReportDTO;
import com.miniproject.rookiejangter.service.BanService;
import com.miniproject.rookiejangter.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final BanService banService;

    /**
     * 사용자 신고 접수
     * POST /api/reports
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReportDTO.Response> createReport(
            @Valid @RequestBody ReportDTO.Request request,
            Authentication authentication
    ) {
        Long reporterId = Long.parseLong(authentication.getName());
        ReportDTO.Response response = reportService.createReport(request, reporterId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * 처리되지 않은 신고 목록 조회 (관리자)
     * GET /api/reports/admin/unprocessed
     */
    @GetMapping("/admin/unprocessed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReportDTO.Response>> getUnprocessedReports() {
        List<ReportDTO.Response> reports = reportService.getUnprocessedReports();
        return ResponseEntity.ok(reports);
    }

    /**
     * 신고 처리 완료로 상태 변경 (관리자)
     * PATCH /api/reports/admin/{reportId}/process
     */
    @PatchMapping("/admin/{reportId}/process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> markReportAsProcessed(@PathVariable Long reportId) {
        reportService.markReportAsProcessed(reportId);
        return ResponseEntity.ok("신고(ID: " + reportId + ")가 처리되었습니다.");
    }

    /**
     * 신고 기반 사용자 제재 (관리자)
     * POST /api/reports/admin/bans
     */
    @PostMapping("/admin/bans")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BanDTO.Response> banUserBasedOnReport(@Valid @RequestBody BanDTO.Request banRequest) {
        BanDTO.Response response = banService.banUser(banRequest.getUserId(), banRequest.getReportId(), banRequest.getBanReason());
        return ResponseEntity.ok(response);
    }
}