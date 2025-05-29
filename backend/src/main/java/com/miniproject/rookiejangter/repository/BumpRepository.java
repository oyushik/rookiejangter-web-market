package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Bump;
import com.miniproject.rookiejangter.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BumpRepository extends JpaRepository<Bump, Long> {
    Optional<Bump> findTopByPost_PostIdOrderByBumpedAtDesc(Long postId);
    List<Bump> findByPost_PostId(Long postId);
    Long countByPost_PostIdAndBumpedAtBetween(Long postId, LocalDateTime start, LocalDateTime end);
}
