package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Category;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.User;
import com.miniproject.rookiejangter.repository.CategoryRepository;
import com.miniproject.rookiejangter.repository.ProductRepository;
import com.miniproject.rookiejangter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category testCategory;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Given
        testCategory = Category.builder()
                .categoryName("Test Category")
                .build();
        testCategory = categoryRepository.save(testCategory);

        testUser = User.builder()
                .loginId("testId")
                .password("testPassword")
                .userName("Test User")
                .phone("010-1234-5678")
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    void createProduct() {
        // Given
        Product product = Product.builder()
                .category(testCategory)
                .user(testUser)
                .title("Test Product Title")
                .content("Test Product Content")
                .price(10000)
                .viewCount(0)
                .isBumped(false)
                .isReserved(false)
                .isCompleted(false)
                .build();

        // When
        Product savedProduct = productRepository.save(product);

        // Then
        assertThat(savedProduct.getProductId()).isNotNull();
        assertThat(savedProduct.getTitle()).isEqualTo("Test Product Title");
    }

    @Test
    void getProductById() {
        // Given
        Product product = Product.builder()
                .category(testCategory)
                .user(testUser)
                .title("Test Product Title")
                .content("Test Product Content")
                .price(10000)
                .viewCount(0)
                .isBumped(false)
                .isReserved(false)
                .isCompleted(false)
                .build();
        product = productRepository.save(product);

        // When
        Product foundProduct = productRepository.findById(product.getProductId()).orElse(null);

        // Then
        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getTitle()).isEqualTo("Test Product Title");
    }

    @Test
    void updateProduct() {
        // Given
        Product product = Product.builder()
                .category(testCategory)
                .user(testUser)
                .title("Test Product Title")
                .content("Test Product Content")
                .price(10000)
                .viewCount(0)
                .isBumped(false)
                .isReserved(false)
                .isCompleted(false)
                .build();
        product = productRepository.save(product);

        // When
        product.setTitle("Updated Product Title");
        Product updatedProduct = productRepository.save(product);

        // Then
        assertThat(updatedProduct.getTitle()).isEqualTo("Updated Product Title");
    }

    @Test
    void deleteProduct() {
        // Given
        Product product = Product.builder()
                .category(testCategory)
                .user(testUser)
                .title("Test Product Title")
                .content("Test Product Content")
                .price(10000)
                .viewCount(0)
                .isBumped(false)
                .isReserved(false)
                .isCompleted(false)
                .build();
        product = productRepository.save(product);

        // When
        productRepository.delete(product);
        Product deletedProduct = productRepository.findById(product.getProductId()).orElse(null);

        // Then
        assertThat(deletedProduct).isNull();
    }

    @Test
    void findByCategory() {
        // Given
        Product product1 = Product.builder()
                .category(testCategory)
                .user(testUser)
                .title("Test Product Title 1")
                .content("Test Product Content 1")
                .price(10000)
                .viewCount(0)
                .isBumped(false)
                .isReserved(false)
                .isCompleted(false)
                .build();
        productRepository.save(product1);

        Product product2 = Product.builder()
                .category(testCategory)
                .user(testUser)
                .title("Test Product Title 2")
                .content("Test Product Content 2")
                .price(10000)
                .viewCount(0)
                .isBumped(false)
                .isReserved(false)
                .isCompleted(false)
                .build();
        productRepository.save(product2);

        // When
        List<Product> products = productRepository.findByCategory(testCategory);

        // Then
        assertThat(products).hasSize(2);
        assertThat(products.get(0).getTitle()).isEqualTo("Test Product Title 1");
        assertThat(products.get(1).getTitle()).isEqualTo("Test Product Title 2");
    }
}