package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Category testCategory;

    @BeforeEach
    public void setup() {
        testCategory = Category.builder()
                .categoryName("Test Category1")
                .build();
        entityManager.persist(testCategory);
        entityManager.flush();
    }

    @Test
    public void testFindByCategoryName() {
        Optional<Category> foundCategory = categoryRepository.findByCategoryName("Test Category1");
        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getCategoryName()).isEqualTo("Test Category1");
    }

    @Test
    public void testFindByCategoryNameContainingIgnoreCase() {
        Category category2 = Category.builder()
                .categoryName("Test Category2")
                .build();
        entityManager.persist(category2);
        entityManager.flush();

        List<Category> foundCategories = categoryRepository.findByCategoryNameContainingIgnoreCase("test");
        assertThat(foundCategories).hasSize(2);
        assertThat(foundCategories.get(0).getCategoryName()).isEqualTo("Test Category1");
        assertThat(foundCategories.get(1).getCategoryName()).isEqualTo("Test Category2");
    }

    @Test
    public void testSaveCategory() {
        Category newCategory = Category.builder()
                .categoryName("New Category")
                .build();
        Category savedCategory = categoryRepository.save(newCategory);
        assertThat(savedCategory.getCategoryId()).isNotNull();
        assertThat(savedCategory.getCategoryName()).isEqualTo("New Category");
    }

    @Test
    public void testDeleteCategory() {
        categoryRepository.delete(testCategory);
        Optional<Category> deletedCategory = categoryRepository.findByCategoryName("Test Category1");
        assertThat(deletedCategory).isEmpty();
    }
}