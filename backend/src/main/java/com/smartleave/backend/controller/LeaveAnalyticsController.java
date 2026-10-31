package com.smartleave.backend.controller;

import com.smartleave.backend.dto.LeaveAnalyticsResponse;
import com.smartleave.backend.service.LeaveAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/hr/reports")
public class LeaveAnalyticsController {

    private final LeaveAnalyticsService leaveAnalyticsService;

    public LeaveAnalyticsController(
            LeaveAnalyticsService leaveAnalyticsService) {
        this.leaveAnalyticsService = leaveAnalyticsService;
    }

    @GetMapping("/leave-analytics")
    public LeaveAnalyticsResponse getLeaveAnalytics(
            @RequestParam(required = false) Integer year) {

        int selectedYear = year == null
                ? LocalDate.now().getYear()
                : year;

        if (selectedYear < 2000 || selectedYear > 2100) {
            throw new IllegalArgumentException(
                    "Year must be between 2000 and 2100");
        }

        return leaveAnalyticsService.getLeaveAnalytics(selectedYear);
    }
}
