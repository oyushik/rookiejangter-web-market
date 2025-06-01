package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReportReasonTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createReportReason() {
        // given
        ReportReason reportReason = ReportReason.builder()
                .reportReasonType("광고성 게시물")
                .build();

        // when
        entityManager.persist(reportReason);
        entityManager.flush();
        entityManager.clear();
        ReportReason savedReportReason = entityManager.find(ReportReason.class, reportReason.getReportReasonId());

        // then
        assertThat(savedReportReason).isNotNull();
        assertThat(savedReportReason.getReportReasonId()).isNotNull();
        assertThat(savedReportReason.getReportReasonType()).isEqualTo("광고성 게시물");
    }

    @Test
    void checkReportReasonReportAssociation() {
        // given
        ReportReason reportReason = ReportReason.builder()
                .reportReasonType("불쾌한 언어 사용")
                .build();
        entityManager.persist(reportReason);

        User reporter = User.builder()
                .loginId("reporter1")
                .password("pwd")
                .userName("신고자1")
                .phone("010-5555-6666")
                .build();
        entityManager.persist(reporter);

        Report report1 = Report.builder()
                .reportReason(reportReason)
                .user(reporter)
                .targetId(100L)
                .targetType("product")
                .reportDetail("욕설이 포함되어 있습니다.")
                .isProcessed(false)
                .build();
        entityManager.persist(report1);

        Report report2 = Report.builder()
                .reportReason(reportReason)
                .user(reporter)
                .targetId(2L)
                .targetType("user")
                .reportDetail("비방적인 발언을 합니다.")
                .isProcessed(false)
                .build();
        entityManager.persist(report2);

        entityManager.flush();
        entityManager.clear();

        // when
        ReportReason foundReportReason = entityManager.find(ReportReason.class, reportReason.getReportReasonId());

        // then
        assertThat(foundReportReason).isNotNull();
        assertThat(foundReportReason.getReports()).hasSize(2);
        assertThat(foundReportReason.getReports()).extracting("reportDetail")
                .containsExactly("욕설이 포함되어 있습니다.", "비방적인 발언을 합니다.");
        assertThat(report1.getReportReason()).isEqualTo(foundReportReason);
        assertThat(report2.getReportReason()).isEqualTo(foundReportReason);
    }
}