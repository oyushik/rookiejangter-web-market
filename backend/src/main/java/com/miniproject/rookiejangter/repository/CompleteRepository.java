package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Complete;
import com.miniproject.rookiejangter.entity.Post;
import com.miniproject.rookiejangter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompleteRepository extends JpaRepository <Complete, Long>{
    Optional<Complete> findByPost(Post post);
    Optional<Complete> findByPost_PostId(Long postId);
    List<Complete> findByBuyer(User buyer);
    List<Complete> findByBuyer_UserId(Long buyerId);
    List<Complete> findBySeller(User seller);
    List<Complete> findBySeller_UserId(Long sellerId);
}
