package entity;

import com.miniproject.rookiejangter.entity.Report;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "report_reasons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_reason_id")
    private Integer reportReasonId;

    @Column(name = "report_reason_type", length = 50)
    private String reportReasonType;

    @OneToMany(mappedBy = "reportReason", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports = new ArrayList<>();

    @Override
    public String toString() {
        return "ReportReason{" +
                "reportReasonId=" + reportReasonId +
                ", reportReasonType='" + reportReasonType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportReason reportReason = (ReportReason) o;
        return reportReasonId != null && reportReasonId.equals(reportReason.reportReasonId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}