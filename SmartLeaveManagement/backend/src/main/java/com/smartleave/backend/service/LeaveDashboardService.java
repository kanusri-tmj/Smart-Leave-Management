package com.smartleave.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.smartleave.backend.dto.LeaveDashboardResponse;
import com.smartleave.backend.entity.Employee;
import com.smartleave.backend.entity.LeaveBalance;
import com.smartleave.backend.entity.LeaveRequest;
import com.smartleave.backend.entity.User;
import com.smartleave.backend.repository.EmployeeRepository;
import com.smartleave.backend.repository.LeaveBalanceRepository;
import com.smartleave.backend.repository.LeaveRequestRepository;

@Service
public class LeaveDashboardService {

    private final EmployeeRepository employeeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveDashboardService(
            EmployeeRepository employeeRepository,
            LeaveBalanceRepository leaveBalanceRepository,
            LeaveRequestRepository leaveRequestRepository) {

        this.employeeRepository = employeeRepository;
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Transactional(readOnly = true)
    public LeaveDashboardResponse getDashboard(
            User user,
            Integer requestedYear) {

        int year = requestedYear != null
                ? requestedYear
                : LocalDate.now().getYear();

        Employee employee = employeeRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee profile not found"));

        if (!employee.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Employee account is inactive");
        }

        List<LeaveBalance> balances =
                leaveBalanceRepository.findByEmployeeIdAndYear(
                        employee.getId(), year);

        List<LeaveRequest> applications =
                leaveRequestRepository.findByEmployeeIdOrderByAppliedAtDesc(
                        employee.getId());

        int totalAllocated = 0;
        int totalUsed = 0;
        int totalPending = 0;

        List<LeaveDashboardResponse.BalanceSummary> balanceSummaries =
                balances.stream()
                        .map(balance -> {
                            int allocated = balance.getAllocatedDays();
                            int used = balance.getUsedDays();
                            int pending = balance.getPendingDays();

                            return new LeaveDashboardResponse.BalanceSummary(
                                    balance.getLeaveType().getName(),
                                    allocated,
                                    used,
                                    pending,
                                    allocated - used - pending
                            );
                        })
                        .toList();

        for (LeaveBalance balance : balances) {
            totalAllocated += balance.getAllocatedDays();
            totalUsed += balance.getUsedDays();
            totalPending += balance.getPendingDays();
        }

        int pendingRequests = 0;
        int approvedRequests = 0;
        int rejectedRequests = 0;
        int cancelledRequests = 0;

        for (LeaveRequest request : applications) {

            if (request.getStartDate().getYear() != year) {
                continue;
            }

            switch (request.getStatus()) {
                case PENDING -> pendingRequests++;
                case APPROVED -> approvedRequests++;
                case REJECTED -> rejectedRequests++;
                case CANCELLED -> cancelledRequests++;
            }
        }

        List<LeaveDashboardResponse.RecentLeaveSummary> recentApplications =
                applications.stream()
                        .filter(request ->
                                request.getStartDate().getYear() == year)
                        .limit(5)
                        .map(request ->
                                new LeaveDashboardResponse.RecentLeaveSummary(
                                        request.getId(),
                                        request.getLeaveType().getName(),
                                        request.getStartDate(),
                                        request.getEndDate(),
                                        request.getTotalDays(),
                                        request.getStatus(),
                                        request.getAppliedAt()
                                ))
                        .toList();

        String departmentName = employee.getDepartment() != null
                ? employee.getDepartment().getName()
                : null;

        return new LeaveDashboardResponse(
                employee.getUser().getFullName(),
                employee.getEmployeeCode(),
                employee.getDesignation(),
                departmentName,
                year,
                totalAllocated,
                totalUsed,
                totalPending,
                totalAllocated - totalUsed - totalPending,
                pendingRequests,
                approvedRequests,
                rejectedRequests,
                cancelledRequests,
                balanceSummaries,
                recentApplications
        );
    }
}