package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Product;
import com.miniproject.rookiejangter.entity.Reservation;
import com.miniproject.rookiejangter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByBuyer_UserId(Long buyerId);
    List<Reservation> findBySeller_UserId(Long sellerId);
    List<Reservation> findByProduct_ProductId(Long productId);

    boolean existsByBuyer_UserIdAndProduct_ProductIdAndIsCanceled(Long buyerUserId, Long productProductId, Boolean isCanceled);
}
