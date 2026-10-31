package com.smartleave.backend.controller;

import com.smartleave.backend.dto.LeaveDashboardResponse;
import com.smartleave.backend.entity.User;
import com.smartleave.backend.service.LeaveDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee/dashboard")
public class LeaveDashboardController {

    private final LeaveDashboardService leaveDashboardService;

    public LeaveDashboardController(
            LeaveDashboardService leaveDashboardService) {
        this.leaveDashboardService = leaveDashboardService;
    }

    @GetMapping
    public ResponseEntity<LeaveDashboardResponse> getDashboard(
            @RequestParam(required = false) Integer year,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                leaveDashboardService.getDashboard(user, year));
    }
}
