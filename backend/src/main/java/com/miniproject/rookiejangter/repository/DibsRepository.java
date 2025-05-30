package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Dibs;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DibsRepository extends JpaRepository<Dibs, Long> {
    List<Dibs> findByUser_UserId(Long userId);
    List<Dibs> findByProduct_ProductId(Long productId);
    boolean existsByUser_UserIdAndProduct_ProductId(Long userId, Long productId);
    void deleteByUser_UserIdAndProduct_ProductId(Long userId, Long productId);

}
