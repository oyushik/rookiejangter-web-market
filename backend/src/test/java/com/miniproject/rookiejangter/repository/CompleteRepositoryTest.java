package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Category;
import com.miniproject.rookiejangter.entity.Complete;
import com.miniproject.rookiejangter.entity.Product;
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
public class CompleteRepositoryTest {

    @Autowired
    private CompleteRepository completeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User buyer;
    private User seller;
    private Category category;
    private Product product1;
    private Product product2;
    private Complete complete;

    @BeforeEach
    void setUp() {
        buyer = User.builder()
                .userName("구매자1")
                .loginId("buyer1")
                .password("password")
                .phone("010-1111-1111")
                .build();
        entityManager.persist(buyer);

        seller = User.builder()
                .userName("판매자1")
                .loginId("seller1")
                .password("password")
                .phone("010-2222-2222")
                .build();
        entityManager.persist(seller);

        category = Category.builder()
                .categoryName("Test Category")
                .build();
        entityManager.persist(category);
        entityManager.flush();

        product1 = Product.builder()
                .category(category)
                .title("Test Product 1")
                .content("Test Content 1")
                .price(10000)
                .user(seller)
                .build();
        entityManager.persist(product1);
        entityManager.flush(); // Product ID를 얻기 위해 flush

        product2 = Product.builder()
                .category(category)
                .title("Test Product 2")
                .content("Test Content 2")
                .price(10000)
                .user(seller)
                .build();
        entityManager.persist(product2);
        entityManager.flush(); // Product ID를 얻기 위해 flush

        complete = Complete.builder()
                .product(product1)
                .buyer(buyer)
                .seller(seller)
                .completedAt(LocalDateTime.now())
                .build();
        entityManager.persist(complete);
        entityManager.flush();
    }

    @Test
    void saveComplete() {
        Complete savedComplete = completeRepository.save(Complete.builder()
                .product(product2)
                .buyer(User.builder().userId(buyer.getUserId()).build())
                .seller(User.builder().userId(seller.getUserId()).build())
                .completedAt(LocalDateTime.now().plusHours(1))
                .build());

        Optional<Complete> foundComplete = completeRepository.findByProduct_ProductId(savedComplete.getProduct().getProductId());
        assertThat(foundComplete).isPresent();
        assertThat(foundComplete.get().getBuyer().getUserId()).isEqualTo(buyer.getUserId());
        assertThat(foundComplete.get().getSeller().getUserId()).isEqualTo(seller.getUserId());
    }

    @Test
    void findByCompleteId() {
        Optional<Complete> foundComplete = completeRepository.findByCompleteId(complete.getCompleteId());
        assertThat(foundComplete).isPresent();
        assertThat(foundComplete.get().getCompleteId()).isEqualTo(complete.getCompleteId());
    }

    @Test
    void findByProductId() {
        Optional<Complete> foundComplete = completeRepository.findByProduct_ProductId(product1.getProductId());
        assertThat(foundComplete).isPresent();
        assertThat(foundComplete.get().getBuyer().getUserId()).isEqualTo(buyer.getUserId());
        assertThat(foundComplete.get().getSeller().getUserId()).isEqualTo(seller.getUserId());
    }

    @Test
    void findByBuyerId() {
        // Complete 엔티티에 buyerId 필드가 있다면 테스트 가능
        // 현재 Complete 엔티티 구조상 buyer 객체를 통해 접근해야 함
        List<Complete> foundComplete = completeRepository.findByBuyer_UserId(buyer.getUserId());
        assertThat(foundComplete).isNotNull();
        assertThat(foundComplete.get(0).getProduct().getProductId()).isEqualTo(product1.getProductId());
    }

    @Test
    void findBySellerId() {
        // Complete 엔티티에 sellerId 필드가 있다면 테스트 가능
        // 현재 Complete 엔티티 구조상 seller 객체를 통해 접근해야 함
        List<Complete> foundComplete = completeRepository.findBySeller_UserId(seller.getUserId());
        assertThat(foundComplete).isNotNull();
        assertThat(foundComplete.get(0).getProduct().getProductId()).isEqualTo(product1.getProductId());
    }
}