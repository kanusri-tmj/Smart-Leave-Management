package com.smartleave.backend.repository;

import com.smartleave.backend.entity.User;
import com.smartleave.backend.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(UserRole role);

    long countByRoleAndActiveTrue(UserRole role);
}
