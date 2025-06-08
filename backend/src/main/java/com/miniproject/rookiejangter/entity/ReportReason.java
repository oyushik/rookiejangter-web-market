package com.miniproject.rookiejangter.entity;

import com.miniproject.rookiejangter.exception.BusinessException;
import com.miniproject.rookiejangter.exception.ErrorCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "report_reasons")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class ReportReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_reason_id")
    private Integer reportReasonId;

    @Column(name = "report_reason_type", length = 50)
    private String reportReasonType;

    @OneToMany(mappedBy = "reportReason", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    // 비즈니스 메서드: 신고 사유 유형 내용 변경
    public void changeReasonType(String newReasonType) {
        if (newReasonType == null || newReasonType.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.REPORT_REASON_EMPTY);
        }
        if (newReasonType.length() > 50) {
            throw new BusinessException(ErrorCode.REPORT_REASON_TOO_LONG);
        }
        this.reportReasonType = newReasonType;
    }
}