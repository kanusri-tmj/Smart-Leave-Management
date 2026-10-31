package com.smartleave.backend.controller;

import com.smartleave.backend.dto.LeaveBalanceRequest;
import com.smartleave.backend.dto.LeaveBalanceResponse;
import com.smartleave.backend.entity.User;
import com.smartleave.backend.service.LeaveBalanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LeaveBalanceController {

    private final LeaveBalanceService balanceService;

    public LeaveBalanceController(LeaveBalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @PostMapping("/hr/leave-balances")
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveBalanceResponse createBalance(
            @Valid @RequestBody LeaveBalanceRequest request) {
        return balanceService.createBalance(request);
    }

    @GetMapping("/hr/leave-balances")
    public List<LeaveBalanceResponse> getAllBalances(
            @RequestParam Integer year) {
        return balanceService.getAllBalances(year);
    }

    @GetMapping("/hr/leave-balances/employee/{employeeId}")
    public List<LeaveBalanceResponse> getEmployeeBalances(
            @PathVariable Long employeeId,
            @RequestParam Integer year) {
        return balanceService.getEmployeeBalances(employeeId, year);
    }

    @PatchMapping("/hr/leave-balances/{id}/allocation")
    public LeaveBalanceResponse updateAllocation(
            @PathVariable Long id,
            @RequestParam Integer allocatedDays) {
        return balanceService.updateAllocation(id, allocatedDays);
    }

    @GetMapping("/employee/leave-balances")
    public List<LeaveBalanceResponse> getMyBalances(
            @RequestParam Integer year,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return balanceService.getMyBalances(user.getId(), year);
    }
}
