package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Image;
import com.miniproject.rookiejangter.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByPost_PostId(Long postId);
    Optional<Image> findByImageUrl(String imageUrl);

    long countByPost_PostId(Long postId);
}

