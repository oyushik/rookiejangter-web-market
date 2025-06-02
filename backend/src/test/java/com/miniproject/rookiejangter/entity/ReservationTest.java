package com.miniproject.rookiejangter.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ReservationTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    void createReservation() {
        // given
        Reservation reservation = Reservation.builder()
                .isCanceled(false)
                .status(Reservation.TradeStatus.REQUESTED)
                .build();

        // when
        entityManager.persist(reservation);
        entityManager.flush();
        entityManager.clear();
        Reservation savedReservation = entityManager.find(Reservation.class, reservation.getReservationId());

        // then
        assertThat(savedReservation).isNotNull();
        assertThat(savedReservation.getReservationId()).isNotNull();
        assertThat(savedReservation.getIsCanceled()).isFalse();
        assertThat(savedReservation.getStatus()).isEqualTo(Reservation.TradeStatus.REQUESTED);
    }

    @Test
    void checkReservationAssociations() {
        // given
        User buyer = User.builder()
                .loginId("buyer1")
                .password("pwd")
                .userName("구매자1")
                .phone("010-1111-1111")
                .build();
        entityManager.persist(buyer);

        User seller = User.builder()
                .loginId("seller1")
                .password("pwd")
                .userName("판매자1")
                .phone("010-2222-2222")
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
                .content("원목 테이블 판매")
                .price(200000)
                .build();
        entityManager.persist(product);

        Reservation reservation = Reservation.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .isCanceled(false)
                .status(Reservation.TradeStatus.REQUESTED)
                .build();
        entityManager.persist(reservation);

        entityManager.flush();
        entityManager.clear();

        // when
        Reservation foundReservation = entityManager.find(Reservation.class, reservation.getReservationId());

        // then
        assertThat(foundReservation).isNotNull();
        assertThat(foundReservation.getBuyer()).isNotNull();
        assertThat(foundReservation.getBuyer().getUserName()).isEqualTo("구매자1");
        assertThat(foundReservation.getSeller()).isNotNull();
        assertThat(foundReservation.getSeller().getUserName()).isEqualTo("판매자1");
        assertThat(foundReservation.getProduct()).isNotNull();
        assertThat(foundReservation.getProduct().getTitle()).isEqualTo("테이블");
        assertThat(foundReservation.getStatus()).isEqualTo(Reservation.TradeStatus.REQUESTED);
    }
}