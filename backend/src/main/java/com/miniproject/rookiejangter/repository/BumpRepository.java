package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Bump;
import com.miniproject.rookiejangter.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BumpRepository extends JpaRepository<Bump, Long> {
    Optional<Bump> findTopByPostOrderByBumpedAtDesc(Post post);
    Optional<Bump> findTopByPost_PostIdOrderByBumpedAtDesc(Long postId);
    List<Bump> findByPost(Post post);
    List<Bump> findByPost_PostId(Long postId);
    Long countByPostAndBumpedAtBetween(Post post, LocalDateTime start, LocalDateTime end);
    Long countByPost_PostIdAndBumpedAtBetween(Long postId, LocalDateTime start, LocalDateTime end);

}
