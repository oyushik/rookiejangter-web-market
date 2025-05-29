package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Report;
import com.miniproject.rookiejangter.entity.ReportReason;
import com.miniproject.rookiejangter.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ReportRepositoryTest {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User reporter1;
    private User targetUser1;
    private ReportReason reason1;
    private Report report1;
    private Report report2;

    @BeforeEach
    void setUp() {
        reporter1 = User.builder()
                .userName("신고자1")
                .loginId("reporter1")
                .password("password")
                .phone("010-1234-1234")
                .build();
        entityManager.persist(reporter1);

        targetUser1 = User.builder()
                .userName("대상자1")
                .loginId("target1")
                .password("password")
                .phone("010-5678-5678")
                .build();
        entityManager.persist(targetUser1);

        reason1 = ReportReason.builder()
                .reportReasonType("불쾌한 언어 사용")
                .build();
        entityManager.persist(reason1);
        entityManager.flush();

        report1 = Report.builder()
                .user(reporter1)
                .targetId(targetUser1.getUserId())
                .targetType("User")
                .reportReason(reason1)
                .reportDetail("욕설 사용")
                .isProcessed(false)
                .build();
        entityManager.persist(report1);

        report2 = Report.builder()
                .user(reporter1)
                .targetId(10L) // 예시 Post ID
                .targetType("Post")
                .reportReason(reason1)
                .reportDetail("게시글 내용 부적절")
                .isProcessed(true)
                .build();
        entityManager.persist(report2);
        entityManager.flush();
    }

    @Test
    void saveReport() {
        Report newReport = Report.builder()
                .user(reporter1)
                .targetId(11L) // 다른 Post ID
                .targetType("Post")
                .reportReason(reason1)
                .reportDetail("광고성 게시글")
                .isProcessed(false)
                .build();
        Report savedReport = reportRepository.save(newReport);

        Optional<Report> foundReport = reportRepository.findById(savedReport.getReportId());
        assertThat(foundReport).isPresent();
        assertThat(foundReport.get().getReportDetail()).isEqualTo("광고성 게시글");
        assertThat(foundReport.get().getUser().getUserId()).isEqualTo(reporter1.getUserId());
        assertThat(foundReport.get().getTargetId()).isEqualTo(11L);
        assertThat(foundReport.get().getTargetType()).isEqualTo("Post");
        assertThat(foundReport.get().getIsProcessed()).isFalse();
        assertThat(foundReport.get().getCreatedAt()).isNotNull();
        assertThat(foundReport.get().getUpdatedAt()).isNotNull();
    }

    @Test
    void findByReportId() {
        Optional<Report> foundReport = reportRepository.findById(report1.getReportId());
        assertThat(foundReport).isPresent();
        assertThat(foundReport.get().getReportDetail()).isEqualTo("욕설 사용");
        assertThat(foundReport.get().getUser().getUserId()).isEqualTo(reporter1.getUserId());
    }

    @Test
    void findByUserId() {
        List<Report> foundReports = reportRepository.findByUser_UserId(reporter1.getUserId());
        assertThat(foundReports).hasSize(2);
        assertThat(foundReports).extracting(Report::getReportDetail)
                .containsExactlyInAnyOrder("욕설 사용", "게시글 내용 부적절");
    }

    @Test
    void findByIsProcessedFalse() {
        List<Report> foundReports = reportRepository.findByIsProcessedFalse();
        assertThat(foundReports).hasSize(1);
        assertThat(foundReports).extracting(Report::getReportDetail)
                .containsExactly("욕설 사용");
    }

    @Test
    void findByIsProcessedTrue() {
        List<Report> foundReports = reportRepository.findByIsProcessedTrue();
        assertThat(foundReports).hasSize(1);
        assertThat(foundReports).extracting(Report::getReportDetail)
                .containsExactly("게시글 내용 부적절");
    }
}