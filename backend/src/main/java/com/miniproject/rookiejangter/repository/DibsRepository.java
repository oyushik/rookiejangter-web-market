package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Dibs;
import com.miniproject.rookiejangter.entity.Post;
import com.miniproject.rookiejangter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DibsRepository extends JpaRepository<Dibs, Long> {
    List<Dibs> findByUser_UserId(Long userId);
    List<Dibs> findByPost_PostId(Long postId);
    boolean existsByUser_UserIdAndPost_PostId(Long userId, Long postId);
    void deleteByUser_UserIdAndPost_PostId(Long userId, Long postId);

}
