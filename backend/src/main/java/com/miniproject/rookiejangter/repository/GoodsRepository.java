package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Goods;
import com.miniproject.rookiejangter.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long> {
    Optional<Goods> findByPost_PostId(Long postId);
}
