package com.smartleave.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartleave.backend.entity.LeaveRequest;
import com.smartleave.backend.enums.LeaveStatus;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeIdOrderByAppliedAtDesc(Long employeeId);

    List<LeaveRequest> findByStatusOrderByAppliedAtAsc(LeaveStatus status);

    List<LeaveRequest> findByEmployeeManagerIdAndStatusOrderByAppliedAtAsc(
            Long managerId, LeaveStatus status);

    List<LeaveRequest> findByEmployeeManagerIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long managerId,
            LeaveStatus status,
            LocalDate startDate,
            LocalDate endDate
    );

    boolean existsByEmployeeIdAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long employeeId,
            List<LeaveStatus> statuses,
            LocalDate endDate,
            LocalDate startDate
    );

    long countByStatus(LeaveStatus status);

    List<LeaveRequest> findTop10ByOrderByAppliedAtDesc();
}
