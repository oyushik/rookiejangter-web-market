package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.CancelationReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CancelationReasonRepository extends JpaRepository<CancelationReason, Integer> {

}