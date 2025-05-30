package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Cancelation;
import com.miniproject.rookiejangter.entity.CancelationReason;
import com.miniproject.rookiejangter.entity.Reservation;
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
public class CancelationRepositoryTest {

    @Autowired
    private CancelationRepository cancelationRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Cancelation cancelation1;
    private Cancelation cancelation2;

    private Reservation reservation1;
    private Reservation reservation2;

    private CancelationReason cancelationReason1;
    private CancelationReason cancelationReason2;

    @BeforeEach
    void setUp() {
        reservation1 = Reservation.builder().build();
        reservation2 = Reservation.builder().build();

        cancelationReason1 = CancelationReason.builder().cancelationReasonType("질병 사유").build();
        cancelationReason2 = CancelationReason.builder().cancelationReasonType("단순 변심").build();

        entityManager.persist(reservation1);
        entityManager.persist(reservation2);
        entityManager.persist(cancelationReason1);
        entityManager.persist(cancelationReason2);
        entityManager.flush();

        cancelation1 = Cancelation.builder()
                .reservation(entityManager.find(Reservation.class, reservation1.getReservationId()))
                .cancelationReason(entityManager.find(CancelationReason.class, cancelationReason1.getCancelationReasonId()))
                .cancelationDetail("Test Cancelation Detail 1")
                .canceledAt(LocalDateTime.now())
                .build();

        cancelation2 = Cancelation.builder()
                .reservation(entityManager.find(Reservation.class, reservation2.getReservationId()))
                .cancelationReason(entityManager.find(CancelationReason.class, cancelationReason2.getCancelationReasonId()))
                .cancelationDetail("Test Cancelation Detail 2")
                .canceledAt(LocalDateTime.now().plusDays(1))
                .build();

        entityManager.persist(cancelation1);
        entityManager.persist(cancelation2);
        entityManager.flush();
    }

    @Test
    void findByReservationId_returnsCancelation() {
        Optional<Cancelation> foundCancelation = cancelationRepository.findByReservationId(cancelation1.getReservationId());
        assertThat(foundCancelation.get().getCancelationReason().getCancelationReasonId()).isEqualTo(cancelation1.getCancelationReason().getCancelationReasonId());
    }

    @Test
    void findByCancelationReason_CancelationReasonId_returnsListOfCancelations() {
        // given
        int searchReasonId = cancelationReason1.getCancelationReasonId(); // 실제 영속화된 객체의 ID 사용

        // when
        List<Cancelation> foundCancelations = cancelationRepository.findByCancelationReason_CancelationReasonId(searchReasonId);

        // then
        System.out.println("Found Cancelations: " + foundCancelations);
        assertThat(foundCancelations).isNotEmpty();
        assertThat(foundCancelations).contains(cancelation1);
    }
}