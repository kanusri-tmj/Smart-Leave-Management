package com.smartleave.backend.repository;

import com.smartleave.backend.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    Optional<LeaveType> findByCode(String code);

    Optional<LeaveType> findByName(String name);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    List<LeaveType> findByActiveTrue();
}
