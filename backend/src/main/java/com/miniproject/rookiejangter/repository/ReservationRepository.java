package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Post;
import com.miniproject.rookiejangter.entity.Reservation;
import com.miniproject.rookiejangter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByBuyer_UserId(Long buyerId);
    List<Reservation> findBySeller_UserId(Long sellerId);
    List<Reservation> findByPost_PostId(Long postId);
    List<Reservation> findByBuyer_UserIdAndPost_PostId(Long buyerUserId, Long postPostId);

    boolean existsByBuyer_UserIdAndPost_PostId(Long buyerId, Long postId);
    void deleteByBuyer_UserIdAndPost_PostId(Long buyerId, Long postId);
}
