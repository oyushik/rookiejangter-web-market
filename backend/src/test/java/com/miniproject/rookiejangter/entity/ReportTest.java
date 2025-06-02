package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReportTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createReport() {
        // given
        Report report = Report.builder()
                .targetId(1L)
                .targetType("user")
                .reportDetail("테스트 신고 내용")
                .isProcessed(false)
                .build();

        // when
        entityManager.persist(report);
        entityManager.flush();
        entityManager.clear();
        Report savedReport = entityManager.find(Report.class, report.getReportId());

        // then
        assertThat(savedReport).isNotNull();
        assertThat(savedReport.getReportId()).isNotNull();
        assertThat(savedReport.getTargetId()).isEqualTo(1L);
        assertThat(savedReport.getTargetType()).isEqualTo("user");
        assertThat(savedReport.getIsProcessed()).isFalse();
    }

    @Test
    void checkReportAssociations() {
        // given
        ReportReason reportReason = ReportReason.builder()
                .reportReasonType("불쾌한 언어 사용")
                .build();
        entityManager.persist(reportReason);

        User reporter = User.builder()
                .loginId("reporter1")
                .password("pwd")
                .userName("신고자1")
                .phone("010-1111-1111")
                .build();
        entityManager.persist(reporter);

        Report report = Report.builder()
                .reportReason(reportReason)
                .user(reporter)
                .targetId(100L)
                .targetType("product")
                .reportDetail("상품 내용이 허위 광고 같습니다.")
                .isProcessed(false)
                .build();
        entityManager.persist(report);

        entityManager.flush();
        entityManager.clear();

        // when
        Report foundReport = entityManager.find(Report.class, report.getReportId());

        // then
        assertThat(foundReport).isNotNull();
        assertThat(foundReport.getReportReason()).isNotNull();
        assertThat(foundReport.getReportReason().getReportReasonType()).isEqualTo("불쾌한 언어 사용");
        assertThat(foundReport.getUser()).isNotNull();
        assertThat(foundReport.getUser().getUserName()).isEqualTo("신고자1");
        assertThat(foundReport.getTargetId()).isEqualTo(100L);
        assertThat(foundReport.getTargetType()).isEqualTo("product");
        assertThat(foundReport.getReportDetail()).isEqualTo("상품 내용이 허위 광고 같습니다.");
        assertThat(foundReport.getIsProcessed()).isFalse();
    }
}