package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Ban;
import com.miniproject.rookiejangter.entity.Report;
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
public class BanRepositoryTest {

    @Autowired
    private BanRepository banRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User bannedUser1;
    private User bannedUser2;
    private Report report1;
    private Report report2;
    private Ban ban1;
    private Ban ban2;

    @BeforeEach
    void setUp() {
        bannedUser1 = User.builder()
                .userName("밴대상1")
                .loginId("ban1")
                .password("password")
                .phone("01012341234")
                .isBanned(true)
                .build();
        entityManager.persist(bannedUser1);

        bannedUser2 = User.builder()
                .userName("밴대상2")
                .loginId("ban2")
                .password("password")
                .phone("01056785678")
                .isBanned(true)
                .build();
        entityManager.persist(bannedUser2);

        report1 = Report.builder()
                .targetId(bannedUser1.getUserId())
                .targetType("User")
                .isProcessed(true)
                .build();
        entityManager.persist(report1);

        report2 = Report.builder()
                .targetId(bannedUser2.getUserId())
                .targetType("User")
                .isProcessed(true)
                .build();
        entityManager.persist(report2);
        entityManager.flush();

        ban1 = Ban.builder()
                .user(bannedUser1)
                .report(report1)
                .banReason("부적절한 활동")
                .build();
        entityManager.persist(ban1);

        ban2 = Ban.builder()
                .user(bannedUser2)
                .report(report2)
                .banReason("커뮤니티 규정 위반")
                .build();
        entityManager.persist(ban2);
        entityManager.flush();
    }

    @Test
    void saveBan() {
        User newUser = User.builder()
                .userName("새로운 밴 대상")
                .loginId("newban")
                .password("password")
                .phone("01034347878")
                .isBanned(true)
                .build();
        entityManager.persist(newUser);

        Report newReport = Report.builder()
                .targetId(newUser.getUserId())
                .targetType("User")
                .isProcessed(true)
                .build();
        entityManager.persist(newReport);

        Ban newBan = Ban.builder()
                .user(newUser)
                .report(newReport)
                .banReason("반복적인 도배")
                .build();
        Ban savedBan = banRepository.save(newBan);

        Optional<Ban> foundBan = banRepository.findById(savedBan.getBanId());
        assertThat(foundBan).isPresent();
        assertThat(foundBan.get().getBanReason()).isEqualTo("반복적인 도배");
        assertThat(foundBan.get().getUser().getUserId()).isEqualTo(newUser.getUserId());
        assertThat(foundBan.get().getReport().getTargetId()).isEqualTo(newUser.getUserId());
        assertThat(foundBan.get().getCreatedAt()).isNotNull();
        assertThat(foundBan.get().getUpdatedAt()).isNotNull();
    }

    @Test
    void findByBanId() {
        Optional<Ban> foundBan = banRepository.findById(ban1.getBanId());
        assertThat(foundBan).isPresent();
        assertThat(foundBan.get().getBanReason()).isEqualTo("부적절한 활동");
        assertThat(foundBan.get().getUser().getUserId()).isEqualTo(bannedUser1.getUserId());
    }

    @Test
    void findByUserId() {
        List<Ban> foundBans = banRepository.findByUser_UserId(bannedUser1.getUserId());
        assertThat(foundBans).hasSize(1);
        assertThat(foundBans.get(0).getBanReason()).isEqualTo("부적절한 활동");
    }

    @Test
    void findByReportId() {
        Optional<Ban> foundBan = banRepository.findByReport_ReportId(report2.getReportId());
        assertThat(foundBan).isPresent();
        assertThat(foundBan.get().getBanReason()).isEqualTo("커뮤니티 규정 위반");
        assertThat(foundBan.get().getUser().getUserId()).isEqualTo(bannedUser2.getUserId());
    }
}