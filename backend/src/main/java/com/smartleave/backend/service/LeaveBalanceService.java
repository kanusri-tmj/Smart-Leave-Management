package com.smartleave.backend.service;

import com.smartleave.backend.dto.LeaveBalanceRequest;
import com.smartleave.backend.dto.LeaveBalanceResponse;
import com.smartleave.backend.entity.Employee;
import com.smartleave.backend.entity.LeaveBalance;
import com.smartleave.backend.entity.LeaveType;
import com.smartleave.backend.repository.EmployeeRepository;
import com.smartleave.backend.repository.LeaveBalanceRepository;
import com.smartleave.backend.repository.LeaveTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class LeaveBalanceService {

    private final LeaveBalanceRepository balanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveBalanceService(LeaveBalanceRepository balanceRepository,
            EmployeeRepository employeeRepository,
            LeaveTypeRepository leaveTypeRepository) {
        this.balanceRepository = balanceRepository;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    @Transactional
    public LeaveBalanceResponse createBalance(LeaveBalanceRequest request) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee not found"));

        if (!employee.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cannot assign balance to an inactive employee");
        }

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Leave type not found"));

        if (!leaveType.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cannot assign an inactive leave type");
        }

        if (balanceRepository.existsByEmployeeIdAndLeaveTypeIdAndYear(
                employee.getId(), leaveType.getId(), request.getYear())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Leave balance already exists for this employee, type and year");
        }

        LeaveBalance balance = new LeaveBalance();
        balance.setEmployee(employee);
        balance.setLeaveType(leaveType);
        balance.setYear(request.getYear());
        balance.setAllocatedDays(request.getAllocatedDays());
        balance.setUsedDays(0);
        balance.setPendingDays(0);

        return toResponse(balanceRepository.save(balance));
    }

    @Transactional
    public LeaveBalanceResponse updateAllocation(Long id, Integer allocatedDays) {

        if (allocatedDays == null || allocatedDays < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Allocated days cannot be negative");
        }

        LeaveBalance balance = findBalance(id);

        if (allocatedDays < balance.getUsedDays() + balance.getPendingDays()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Allocation cannot be lower than used days plus pending days");
        }

        balance.setAllocatedDays(allocatedDays);
        return toResponse(balanceRepository.save(balance));
    }

    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> getEmployeeBalances(Long employeeId, Integer year) {

        return balanceRepository.findByEmployeeIdAndYear(employeeId, year)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> getAllBalances(Integer year) {

        return balanceRepository.findByYear(year)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> getMyBalances(Long userId, Integer year) {

        Employee employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Employee profile not found"));

        return getEmployeeBalances(employee.getId(), year);
    }

    private LeaveBalance findBalance(Long id) {
        return balanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Leave balance not found"));
    }

    private LeaveBalanceResponse toResponse(LeaveBalance balance) {

        Employee employee = balance.getEmployee();
        LeaveType leaveType = balance.getLeaveType();

        int available = balance.getAllocatedDays()
                - balance.getUsedDays()
                - balance.getPendingDays();

        return new LeaveBalanceResponse(
                balance.getId(),
                employee.getId(),
                employee.getUser().getFullName(),
                employee.getEmployeeCode(),
                leaveType.getId(),
                leaveType.getName(),
                leaveType.getCode(),
                balance.getYear(),
                balance.getAllocatedDays(),
                balance.getUsedDays(),
                balance.getPendingDays(),
                available,
                balance.getUpdatedAt()
        );
    }
}
