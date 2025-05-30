package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.Reservation;
import com.miniproject.rookiejangter.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User buyer;
    private User seller;
    private Product product;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        // Given
        buyer = User.builder()
                .loginId("buyer123")
                .password("password")
                .userName("Buyer User")
                .phone("010-1234-1234")
                .build();
        seller = User.builder()
                .loginId("seller456")
                .password("password")
                .userName("Seller User")
                .phone("010-5678-5678")
                .build();
        product = Product.builder()
                .title("Test Product")
                .content("Test Content")
                .price(10000)
                .build();

        entityManager.persist(buyer);
        entityManager.persist(seller);
        entityManager.persist(product);

        reservation = Reservation.builder()
                .buyer(buyer)
                .seller(seller)
                .product(product)
                .isCanceled(false)
                .build();

        entityManager.persist(reservation);
        entityManager.flush();
    }

    @Test
    void findByBuyer_UserId() {
        // When
        List<Reservation> foundReservations = reservationRepository.findByBuyer_UserId(buyer.getUserId());

        // Then
        assertThat(foundReservations).hasSize(1);
        assertThat(foundReservations.get(0).getBuyer().getUserId()).isEqualTo(buyer.getUserId());
    }

    @Test
    void findBySeller_UserId() {
        // When
        List<Reservation> foundReservations = reservationRepository.findBySeller_UserId(seller.getUserId());

        // Then
        assertThat(foundReservations).hasSize(1);
        assertThat(foundReservations.get(0).getSeller().getUserId()).isEqualTo(seller.getUserId());
    }

    @Test
    void findByProduct_ProductId() {
        // When
        List<Reservation> foundReservations = reservationRepository.findByProduct_ProductId(product.getProductId());

        // Then
        assertThat(foundReservations).hasSize(1);
        assertThat(foundReservations.get(0).getProduct().getProductId()).isEqualTo(product.getProductId());
    }

    @Test
    void findByBuyer_UserIdAndProduct_ProductId() {
        // When
        List<Reservation> foundReservation = reservationRepository.findByBuyer_UserIdAndProduct_ProductId(buyer.getUserId(), product.getProductId());

        // Then
        assertThat(foundReservation).isNotNull();
        assertThat(foundReservation.get(0).getBuyer().getUserId()).isEqualTo(buyer.getUserId());
        assertThat(foundReservation.get(0).getProduct().getProductId()).isEqualTo(product.getProductId());
    }

    @Test
    void existsByBuyer_UserIdAndProduct_ProductId_shouldReturnTrue_ifExists() {
        // When
        boolean exists = reservationRepository.existsByBuyer_UserIdAndProduct_ProductId(buyer.getUserId(), product.getProductId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByBuyer_UserIdAndProduct_ProductId_shouldReturnFalse_ifNotExists() {
        // Given
        User otherBuyer = User.builder()
                .loginId("otherBuyer")
                .password("password")
                .userName("Other Buyer")
                .phone("010-9999-9999")
                .build();
        entityManager.persist(otherBuyer);

        // When
        boolean exists = reservationRepository.existsByBuyer_UserIdAndProduct_ProductId(otherBuyer.getUserId(), product.getProductId());

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void deleteByBuyer_UserIdAndProduct_ProductId() {
        // When
        reservationRepository.deleteByBuyer_UserIdAndProduct_ProductId(buyer.getUserId(), product.getProductId());
        List<Reservation> deletedReservation = reservationRepository.findByBuyer_UserIdAndProduct_ProductId(buyer.getUserId(), product.getProductId());

        // Then
        assertThat(deletedReservation).isEmpty();
    }
}