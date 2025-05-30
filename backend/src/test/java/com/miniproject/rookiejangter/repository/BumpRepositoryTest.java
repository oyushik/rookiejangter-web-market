package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Bump;
import com.miniproject.rookiejangter.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BumpRepositoryTest {

    @Autowired
    private BumpRepository bumpRepository;

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;
    private Bump testBump1;
    private Bump testBump2;

    @BeforeEach
    void setUp() {
        // Given
        testProduct = Product.builder()
                .title("Test Product")
                .content("Test Content")
                .price(10000)
                .build();
        productRepository.save(testProduct);

        testBump1 = Bump.builder()
                .product(testProduct)
                .bumpedAt(LocalDateTime.now().minusDays(2))
                .bumpCount(1)
                .build();

        testBump2 = Bump.builder()
                .product(testProduct)
                .bumpedAt(LocalDateTime.now())
                .bumpCount(2)
                .build();

        bumpRepository.save(testBump1);
        bumpRepository.save(testBump2);
    }

    @Test
    void findTopByProduct_ProductIdOrderByBumpedAtDesc() {
        // When
        Optional<Bump> latestBump = bumpRepository.findTopByProduct_ProductIdOrderByBumpedAtDesc(testProduct.getProductId());

        // Then
        assertThat(latestBump).isPresent();
        assertThat(latestBump.get().getBumpCount()).isEqualTo(2);
    }

    @Test
    void findByProduct_ProductId() {
        // When
        List<Bump> bumps = bumpRepository.findByProduct_ProductId(testProduct.getProductId());

        // Then
        assertThat(bumps).hasSize(2);
        assertThat(bumps.get(0).getProduct().getProductId()).isEqualTo(testProduct.getProductId());
    }

    @Test
    void countByProduct_ProductIdAndBumpedAtBetween() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // When
        Long count = bumpRepository.countByProduct_ProductIdAndBumpedAtBetween(testProduct.getProductId(), start, end);

        // Then
        assertThat(count).isEqualTo(2);
    }
}