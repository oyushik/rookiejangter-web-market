package com.miniproject.rookiejangter.repository;

import com.miniproject.rookiejangter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByLoginId(String loginId);
    boolean existsByLoginId(String loginId);
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);
    List<User> findByIsBannedTrue();
    List<User> findByIsAdminTrue();
}
