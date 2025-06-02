package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CancelationReasonTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createCancelationReason() {
        // given
        CancelationReason cancelationReason = CancelationReason.builder()
                .cancelationReasonType("단순 변심")
                .build();

        // when
        entityManager.persist(cancelationReason);
        entityManager.flush();
        entityManager.clear();
        CancelationReason savedCancelationReason = entityManager.find(CancelationReason.class, cancelationReason.getCancelationReasonId());

        // then
        assertThat(savedCancelationReason).isNotNull();
        assertThat(savedCancelationReason.getCancelationReasonId()).isNotNull();
        assertThat(savedCancelationReason.getCancelationReasonType()).isEqualTo("단순 변심");
    }
}