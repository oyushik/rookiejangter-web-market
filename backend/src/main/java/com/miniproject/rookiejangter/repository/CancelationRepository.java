package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.Cancelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CancelationRepository extends JpaRepository<Cancelation, Long> {
    Optional<Cancelation> findByReservationId(Long reservationId);
    List<Cancelation> findByCancelationReason_CancelationReasonId(Integer cancelationReasonCancelationReasonId);
}