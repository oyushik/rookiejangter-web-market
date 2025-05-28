package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Post;
import com.miniproject.rookiejangter.entity.Reservation;
import com.miniproject.rookiejangter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByBuyer(User buyer);
    List<Reservation> findByBuyer_UserId(Long buyerId);
    List<Reservation> findBySeller(User seller);
    List<Reservation> findBySeller_UserId(Long sellerId);
    List<Reservation> findByPost(Post post);
    List<Reservation> findByPost_PostId(Long postId);
    Optional<Reservation> findByBuyerAndPost(User buyer, Post post);
    boolean existsByBuyer_UserIdAndPost_PostId(Long buyerId, Long postId);
    void deleteByBuyer_UserIdAndPost_PostId(Long buyerId, Long postId);
}
