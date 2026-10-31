package com.smartleave.backend.controller;

import com.smartleave.backend.dto.LeaveCancellationResponse;
import com.smartleave.backend.entity.User;
import com.smartleave.backend.service.LeaveCancellationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee/leave-applications")
public class LeaveCancellationController {

    private final LeaveCancellationService leaveCancellationService;

    public LeaveCancellationController(
            LeaveCancellationService leaveCancellationService) {
        this.leaveCancellationService = leaveCancellationService;
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<LeaveCancellationResponse> cancelLeave(
            @PathVariable Long id,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                leaveCancellationService.cancelLeave(id, user));
    }
}
