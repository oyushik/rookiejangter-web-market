package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.ReportReason;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ReportReasonRepositoryTest {

    @Autowired
    private ReportReasonRepository reportReasonRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ReportReason reportReason1;
    private ReportReason reportReason2;

    @BeforeEach
    void setUp() {
        reportReason1 = ReportReason.builder()
                .reportReasonType("불쾌한 언어 사용")
                .build();
        entityManager.persist(reportReason1);

        reportReason2 = ReportReason.builder()
                .reportReasonType("사기 의심")
                .build();
        entityManager.persist(reportReason2);
        entityManager.flush();
    }

    @Test
    void saveReportReason() {
        ReportReason newReportReason = ReportReason.builder()
                .reportReasonType("도배")
                .build();
        ReportReason savedReportReason = reportReasonRepository.save(newReportReason);

        Optional<ReportReason> foundReportReason = reportReasonRepository.findById(savedReportReason.getReportReasonId());
        assertThat(foundReportReason).isPresent();
        assertThat(foundReportReason.get().getReportReasonType()).isEqualTo("도배");
    }

    @Test
    void findByReportReasonId() {
        Optional<ReportReason> foundReportReason = reportReasonRepository.findById(reportReason1.getReportReasonId());
        assertThat(foundReportReason).isPresent();
        assertThat(foundReportReason.get().getReportReasonType()).isEqualTo("불쾌한 언어 사용");
    }
}