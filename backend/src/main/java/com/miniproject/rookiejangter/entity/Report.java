package com.miniproject.rookiejangter.entity;

import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "reports")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@EqualsAndHashCode
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_reason_id")
    private ReportReason reportReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "target_id")
    private Long targetId;

    @Size(max = 10, message = "신고 대상 타입은 최대 20자까지 가능합니다.")
    @Column(name = "target_type", length = 20)
    private String targetType;

    @Size(max = 255, message = "신고 상세는 최대 255자까지 가능합니다.")
    @Column(name = "report_detail", length = 255)
    private String reportDetail;

    @Column(name = "is_processed")
    private Boolean isProcessed;

    /**
     * 신고 정보를 업데이트합니다.
     *
     * @param newReportReason 신고 사유
     * @param newTargetId 신고 대상 ID
     * @param newTargetType 신고 대상 타입
     * @param newReportDetail 신고 상세 정보
     */
    public void updateReportInfo(ReportReason newReportReason, Long newTargetId, String newTargetType, String newReportDetail) {
        if (newReportReason == null) {
            throw new BusinessException(ErrorCode.REPORT_REASON_EMPTY);
        }
        if (newTargetId == null) {
            throw new BusinessException(ErrorCode.REPORT_TARGET_ID_EMPTY);
        }
        if (newTargetType == null || newTargetType.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.REPORT_TARGET_TYPE_EMPTY);
        }
        if (newTargetType.length() > 20) {
            throw new BusinessException(ErrorCode.REPORT_TARGET_TYPE_TOO_LONG);
        }
        if (newReportDetail != null && newReportDetail.length() > 255) {
            throw new BusinessException(ErrorCode.REPORT_REASON_TOO_LONG);
        }

        this.reportReason = newReportReason;
        this.targetId = newTargetId;
        this.targetType = newTargetType;
        this.reportDetail = newReportDetail;
    }

    // 신고 처리 상태 변경
    public void markAsProcessed() {
        if (!this.isProcessed) {
            this.isProcessed = true;
        }
    }

}