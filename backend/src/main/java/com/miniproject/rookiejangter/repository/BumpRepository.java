package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Bump;
import com.miniproject.rookiejangter.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BumpRepository extends JpaRepository<Bump, Long> {
    Optional<Bump> findTopByProduct_ProductIdOrderByBumpedAtDesc(Long productId);
    List<Bump> findByProduct_ProductId(Long productId);
    Long countByProduct_ProductIdAndBumpedAtBetween(Long productId, LocalDateTime start, LocalDateTime end);
}
