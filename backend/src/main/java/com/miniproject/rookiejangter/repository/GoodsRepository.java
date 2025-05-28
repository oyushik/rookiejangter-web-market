package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Goods;
import com.miniproject.rookiejangter.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoodsRepository extends JpaRepository<Goods, Long> {
    Optional<Goods> findByPost(Post post);
    Optional<Goods> findByPost_PostId(Long postId);
}
