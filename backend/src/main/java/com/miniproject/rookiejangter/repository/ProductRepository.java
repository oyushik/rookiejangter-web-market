package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Category;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByTitleContainsIgnoreCase(String title);
    List<Product> findByTitleContainsIgnoreCaseOrContentContainsIgnoreCase(String titleKeyword, String contentKeyword);
    List<Product> findByIsReservedTrue();
    List<Product> findByIsReservedFalse();
    List<Product> findByIsCompletedTrue();
    List<Product> findByIsCompletedFalse();
    Page<Product> findByUser(User user, Pageable pageable);
    Page<Product> findByCategory(Category category, Pageable pageable);
    Page<Product> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
