package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Image;
import com.miniproject.rookiejangter.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByPost(Post post);
    List<Image> findByPost_PostId(Long postId);
    Optional<Image> findByImageUrl(String imageUrl);

    long countByPost(Post post);
    long countByPost_PostId(Long postId);
}

