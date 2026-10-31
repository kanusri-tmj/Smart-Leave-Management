package com.smartleave.backend.controller;

import com.smartleave.backend.dto.LeaveApplicationRequest;
import com.smartleave.backend.dto.LeaveApplicationResponse;
import com.smartleave.backend.entity.User;
import com.smartleave.backend.service.LeaveApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/leave-applications")
public class LeaveApplicationController {

    private final LeaveApplicationService applicationService;

    public LeaveApplicationController(LeaveApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveApplicationResponse applyForLeave(
            @Valid @RequestBody LeaveApplicationRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return applicationService.applyForLeave(user.getId(), request);
    }

    @GetMapping
    public List<LeaveApplicationResponse> getMyApplications(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return applicationService.getMyApplications(user.getId());
    }

    @GetMapping("/{id}")
    public LeaveApplicationResponse getMyApplication(
            @PathVariable Long id,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return applicationService.getMyApplication(user.getId(), id);
    }
}
