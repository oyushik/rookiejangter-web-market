package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByCategoryName(String categoryName);
    List<Category> findByCategoryNameContainingIgnoreCase(String categoryName);
}
