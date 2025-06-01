package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BanTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createBan() {
        // given
        Ban ban = Ban.builder()
                .banReason("테스트 밴 사유")
                .build();

        // when
        entityManager.persist(ban);
        entityManager.flush();
        entityManager.clear();
        Ban savedBan = entityManager.find(Ban.class, ban.getBanId());

        // then
        assertThat(savedBan).isNotNull();
        assertThat(savedBan.getBanId()).isNotNull();
        assertThat(savedBan.getBanReason()).isEqualTo("테스트 밴 사유");
    }

    @Test
    void checkBanAssociations() {
        // given
        User bannedUser = User.builder()
                .loginId("banneduser")
                .password("pwd")
                .userName("밴유저")
                .phone("010-1111-1111")
                .build();
        entityManager.persist(bannedUser);

        ReportReason reportReason = ReportReason.builder()
                .reportReasonType("도배")
                .build();
        entityManager.persist(reportReason);

        User reporter = User.builder()
                .loginId("reporter")
                .password("pwd")
                .userName("신고자")
                .phone("010-2222-2222")
                .build();
        entityManager.persist(reporter);

        Report report = Report.builder()
                .reportReason(reportReason)
                .user(reporter)
                .targetId(bannedUser.getUserId())
                .targetType("user")
                .reportDetail("도배 행위")
                .isProcessed(true)
                .build();
        entityManager.persist(report);

        Ban ban = Ban.builder()
                .user(bannedUser)
                .report(report)
                .banReason("도배 행위로 인한 밴")
                .build();
        entityManager.persist(ban);

        entityManager.flush();
        entityManager.clear();

        // when
        Ban foundBan = entityManager.find(Ban.class, ban.getBanId());

        // then
        assertThat(foundBan).isNotNull();
        assertThat(foundBan.getUser()).isNotNull();
        assertThat(foundBan.getUser().getUserName()).isEqualTo("밴유저");
        assertThat(foundBan.getReport()).isNotNull();
        assertThat(foundBan.getReport().getReportDetail()).isEqualTo("도배 행위");
        assertThat(foundBan.getBanReason()).isEqualTo("도배 행위로 인한 밴");
    }
}