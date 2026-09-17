package com.example.userservice.repository;

import com.example.userservice.entity.AccountStatus;
import com.example.userservice.entity.User;
import com.example.userservice.entity.UserRole;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByRole(UserRole role);

  Page<User> findByRoleAndStatus(UserRole role, AccountStatus status, Pageable pageable);
}
