package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Chat;
import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Page<Chat> findByBuyer_UserIdOrSeller_UserId(Long buyerUserId, Long sellerUserId, Pageable pageable);
    Optional<Chat> findByBuyer_UserIdAndSeller_UserIdAndProduct_ProductId(Long buyerUserId, Long sellerUserId, Long productProductId);
}