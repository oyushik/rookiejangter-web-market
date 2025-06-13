package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByBuyer_UserId(Long buyerUserId);
    List<Chat> findBySeller_UserId(Long sellerUserId);
    List<Chat> findByProduct_ProductId(Long productProductId);
    Page<Chat> findByBuyer_UserIdOrSeller_UserId(Long buyerUserId, Long sellerUserId, Pageable pageable);
}