package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Category;
import com.miniproject.rookiejangter.entity.Post;
import com.miniproject.rookiejangter.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByTitleContainsIgnoreCase(String title);
    List<Post> findByContentContainsIgnoreCase(String content);
    List<Post> findByTitleContainsIgnoreCaseOrContentContainsIgnoreCase(String titleKeyword, String contentKeyword);
    List<Post> findByViewCountGreaterThanEqual(Integer viewCount);
    List<Post> findByUser(User user);
    List<Post> findByUser_UserId(Long userId);
    List<Post> findByCategory(Category category);
    List<Post> findByCategoryCategoryId(Integer categoryId);
    //Page<Post> findByStatus(PostStatus status, Pageable pageable);
    List<Post> findByIsReservedTrue();
    List<Post> findByIsReservedFalse();
    List<Post> findByIsCompletedTrue();
    List<Post> findByIsCompletedFalse();
    List<Post> findByIsBumpedTrue();
    Page<Post> findByUser(User user, Pageable pageable);
    Page<Post> findByCategory(Category category, Pageable pageable);
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    long countByUser(User user);
    long countByUser_UserId(Long userId);
}
