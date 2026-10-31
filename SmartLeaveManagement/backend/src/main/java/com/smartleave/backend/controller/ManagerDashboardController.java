package com.smartleave.backend.controller;

import com.smartleave.backend.dto.ManagerDashboardResponse;
import com.smartleave.backend.entity.User;
import com.smartleave.backend.service.ManagerDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/manager/dashboard")
public class ManagerDashboardController {

    private final ManagerDashboardService managerDashboardService;

    public ManagerDashboardController(
            ManagerDashboardService managerDashboardService) {
        this.managerDashboardService = managerDashboardService;
    }

    @GetMapping
    public ResponseEntity<ManagerDashboardResponse> getDashboard(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                managerDashboardService.getDashboard(user));
    }
}
