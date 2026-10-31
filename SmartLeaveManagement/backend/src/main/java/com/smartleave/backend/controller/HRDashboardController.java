package com.smartleave.backend.controller;

import com.smartleave.backend.dto.HRDashboardResponse;
import com.smartleave.backend.service.HRDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr/dashboard")
public class HRDashboardController {

    private final HRDashboardService hrDashboardService;

    public HRDashboardController(HRDashboardService hrDashboardService) {
        this.hrDashboardService = hrDashboardService;
    }

    @GetMapping
    public ResponseEntity<HRDashboardResponse> getDashboard() {
        return ResponseEntity.ok(hrDashboardService.getDashboard());
    }
}
