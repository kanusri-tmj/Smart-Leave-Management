package com.smartleave.backend.service;

import com.smartleave.backend.dto.ManagerDashboardResponse;
import com.smartleave.backend.entity.Employee;
import com.smartleave.backend.entity.LeaveRequest;
import com.smartleave.backend.entity.User;
import com.smartleave.backend.enums.LeaveStatus;
import com.smartleave.backend.repository.EmployeeRepository;
import com.smartleave.backend.repository.LeaveRequestRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ManagerDashboardService {

    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public ManagerDashboardService(
            EmployeeRepository employeeRepository,
            LeaveRequestRepository leaveRequestRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Transactional(readOnly = true)
    public ManagerDashboardResponse getDashboard(User user) {

        Employee manager = employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Manager employee profile not found"));

        if (!manager.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Manager account is inactive");
        }

        List<Employee> teamMembers =
                employeeRepository.findByManagerId(manager.getId());

        List<LeaveRequest> pendingRequests =
                leaveRequestRepository
                        .findByEmployeeManagerIdAndStatusOrderByAppliedAtAsc(
                                manager.getId(), LeaveStatus.PENDING);

        List<LeaveRequest> employeesOnLeave =
                leaveRequestRepository
                        .findByEmployeeManagerIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                                manager.getId(),
                                LeaveStatus.APPROVED,
                                LocalDate.now(),
                                LocalDate.now());

        int activeMembers = (int) teamMembers.stream()
                .filter(Employee::isActive)
                .count();

        List<ManagerDashboardResponse.TeamMemberSummary> memberSummaries =
                teamMembers.stream()
                        .map(employee ->
                                new ManagerDashboardResponse.TeamMemberSummary(
                                        employee.getId(),
                                        employee.getUser().getFullName(),
                                        employee.getEmployeeCode(),
                                        employee.getDesignation(),
                                        employee.isActive()
                                ))
                        .toList();

        List<ManagerDashboardResponse.PendingLeaveSummary> pendingSummaries =
                pendingRequests.stream()
                        .limit(10)
                        .map(request ->
                                new ManagerDashboardResponse.PendingLeaveSummary(
                                        request.getId(),
                                        request.getEmployee().getUser().getFullName(),
                                        request.getEmployee().getEmployeeCode(),
                                        request.getLeaveType().getName(),
                                        request.getStartDate(),
                                        request.getEndDate(),
                                        request.getTotalDays(),
                                        request.getReason(),
                                        request.getAppliedAt()
                                ))
                        .toList();

        String departmentName = manager.getDepartment() != null
                ? manager.getDepartment().getName()
                : null;

        return new ManagerDashboardResponse(
                manager.getUser().getFullName(),
                departmentName,
                teamMembers.size(),
                activeMembers,
                pendingRequests.size(),
                employeesOnLeave.size(),
                memberSummaries,
                pendingSummaries
        );
    }
}
