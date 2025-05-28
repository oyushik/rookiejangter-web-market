package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.CancelationReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancelationReasonRepository extends JpaRepository<CancelationReason, Integer> {
    CancelationReason findByCancelationReasonId(Integer cancelationReasonId);
    CancelationReason findByCancelationReasonType(String cancelationReasonType);
}