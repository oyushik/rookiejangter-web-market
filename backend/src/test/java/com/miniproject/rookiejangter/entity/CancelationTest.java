package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CancelationTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createCancelation() {
        // given
        User buyer = User.builder()
                .loginId("buyer")
                .password("pwd")
                .userName("구매자")
                .phone("010-1111-1111")
                .build();
        entityManager.persist(buyer);

        User seller = User.builder()
                .loginId("seller")
                .password("pwd")
                .userName("판매자")
                .phone("010-2222-2222")
                .build();
        entityManager.persist(seller);

        Category category = Category.builder()
                .categoryName("의류")
                .build();
        entityManager.persist(category);

        Product product = Product.builder()
                .user(seller)
                .category(category)
                .title("티셔츠")
                .content("테스트 티셔츠")
                .price(20000)
                .build();
        entityManager.persist(product);

        Reservation reservation = Reservation.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .status(Reservation.TradeStatus.REQUESTED)
                .build();
        entityManager.persist(reservation);

        CancelationReason cancelationReason = CancelationReason.builder()
                .cancelationReasonType("사이즈 미스")
                .build();
        entityManager.persist(cancelationReason);

        Cancelation cancelation = Cancelation.builder()
                .reservation(reservation)
                .cancelationReason(cancelationReason)
                .cancelationDetail("사이즈가 맞지 않아서 취소합니다.")
                .canceledAt(LocalDateTime.now())
                .build();

        // when
        entityManager.persist(cancelation);
        entityManager.flush();
        entityManager.clear();
        Cancelation savedCancelation = entityManager.find(Cancelation.class, cancelation.getCancelationId());

        // then
        assertThat(savedCancelation).isNotNull();
        assertThat(savedCancelation.getCancelationId()).isNotNull();
        assertThat(savedCancelation.getReservation()).isNotNull();
        assertThat(savedCancelation.getReservation().getProduct().getTitle()).isEqualTo("티셔츠");
        assertThat(savedCancelation.getCancelationReason()).isNotNull();
        assertThat(savedCancelation.getCancelationReason().getCancelationReasonType()).isEqualTo("사이즈 미스");
        assertThat(savedCancelation.getCanceledAt()).isNotNull();
        assertThat(savedCancelation.getCancelationDetail()).isEqualTo("사이즈가 맞지 않아서 취소합니다.");
    }

    @Test
    void checkCancelationAssociations() {
        // given
        User buyer = User.builder()
                .loginId("buyer2")
                .password("pwd")
                .userName("구매자2")
                .phone("010-3333-3333")
                .build();
        entityManager.persist(buyer);

        User seller = User.builder()
                .loginId("seller2")
                .password("pwd")
                .userName("판매자2")
                .phone("010-4444-4444")
                .build();
        entityManager.persist(seller);

        Category category = Category.builder()
                .categoryName("가구")
                .build();
        entityManager.persist(category);

        Product product = Product.builder()
                .user(seller)
                .category(category)
                .title("테이블")
                .content("원목 테이블")
                .price(300000)
                .build();
        entityManager.persist(product);

        Reservation reservation = Reservation.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .status(Reservation.TradeStatus.ACCEPTED)
                .build();
        entityManager.persist(reservation);

        CancelationReason cancelationReason = CancelationReason.builder()
                .cancelationReasonType("개인적인 사정")
                .build();
        entityManager.persist(cancelationReason);

        Cancelation cancelation = Cancelation.builder()
                .reservation(reservation)
                .cancelationReason(cancelationReason)
                .cancelationDetail("더 이상 필요 없게 되었습니다.")
                .canceledAt(LocalDateTime.now())
                .build();
        entityManager.persist(cancelation);

        entityManager.flush();
        entityManager.clear();

        // when
        Cancelation foundCancelation = entityManager.find(Cancelation.class, cancelation.getCancelationId());

        // then
        assertThat(foundCancelation).isNotNull();
        assertThat(foundCancelation.getCancelationId()).isNotNull();
        assertThat(foundCancelation.getReservation()).isNotNull();
        assertThat(foundCancelation.getReservation().getBuyer().getUserName()).isEqualTo("구매자2");
        assertThat(foundCancelation.getCancelationReason()).isNotNull();
        assertThat(foundCancelation.getCancelationReason().getCancelationReasonType()).isEqualTo("개인적인 사정");
        assertThat(foundCancelation.getCanceledAt()).isNotNull();
        assertThat(foundCancelation.getCancelationDetail()).isEqualTo("더 이상 필요 없게 되었습니다.");
    }
}