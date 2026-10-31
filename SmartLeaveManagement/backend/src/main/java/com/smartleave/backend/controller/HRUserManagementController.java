package com.smartleave.backend.controller;

import com.smartleave.backend.dto.HRUserResponse;
import com.smartleave.backend.dto.UserRoleUpdateRequest;
import com.smartleave.backend.dto.UserStatusUpdateRequest;
import com.smartleave.backend.entity.User;
import com.smartleave.backend.service.HRUserManagementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hr/users")
public class HRUserManagementController {

    private final HRUserManagementService userManagementService;

    public HRUserManagementController(
            HRUserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping
    public ResponseEntity<List<HRUserResponse>> getAllUsers() {
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<HRUserResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleUpdateRequest request,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                userManagementService.updateRole(
                        id, request.getRole(), currentUser));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<HRUserResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateRequest request,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                userManagementService.updateStatus(
                        id, request.getActive(), currentUser));
    }
}
