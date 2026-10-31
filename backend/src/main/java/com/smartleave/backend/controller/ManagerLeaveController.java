package com.smartleave.backend.controller;

import com.smartleave.backend.dto.LeaveApplicationResponse;
import com.smartleave.backend.dto.LeaveDecisionRequest;
import com.smartleave.backend.entity.User;
import com.smartleave.backend.service.ManagerLeaveService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/leave-applications")
public class ManagerLeaveController {

    private final ManagerLeaveService managerLeaveService;

    public ManagerLeaveController(ManagerLeaveService managerLeaveService) {
        this.managerLeaveService = managerLeaveService;
    }

    @GetMapping("/pending")
    public List<LeaveApplicationResponse> getPendingRequests(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return managerLeaveService.getPendingRequests(user);
    }

    @PatchMapping("/{id}/decision")
    public LeaveApplicationResponse decideRequest(
            @PathVariable Long id,
            @Valid @RequestBody LeaveDecisionRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return managerLeaveService.decideRequest(id, request, user);
    }
}
